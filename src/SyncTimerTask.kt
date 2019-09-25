package nl.teqplay.trelloextension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TimeHelper.getISODateForToday
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.model.SprintLists
import nl.teqplay.trelloextension.model.sync.BoardSyncConfig
import nl.teqplay.trelloextension.model.sync.SyncConfig
import nl.teqplay.trelloextension.service.card.SyncCardsOfLists
import nl.teqplay.trelloextension.service.list.GetBoardLists
import nl.teqplay.trelloextension.service.sync.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.sync.SyncMembers
import nl.teqplay.trelloextension.service.sync.SyncTeamStatistics
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ScheduledExecutorService


class SyncTimerTask(
    private val scheduler: ScheduledExecutorService,
    private val zonedDateTime: ZonedDateTime
) : TimerTask() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing sync timer task")
        CoroutineScope(IO).launch {
            val config = ConfigDataSource.getSyncConfig(Database.instance)
            if (config != null) {
                val stringToday = getISODateForToday()

                for (board in config.boards) {
                    val lists = GetBoardLists(
                        board.id,
                        config.key,
                        config.token
                    ).execute()

                    var niceToHaveListId = ""
                    var prioListId = ""
                    var doingListId = ""
                    var reviewingListId = ""
                    var testingListId = ""
                    var readyListId = ""
                    var impedimentsListId = ""
                    var doneListId = ""

                    val potentialDoneLists = mutableListOf<List>()

                    for (list in lists) {
                        when (list.name.toLowerCase()) {
                            Constants.NICETOHAVE_LIST_NAME -> niceToHaveListId = list.id
                            Constants.PRIO_LIST_NAME -> prioListId = list.id
                            Constants.DOING_LIST_NAME -> doingListId = list.id
                            Constants.REVIEWING_LIST_NAME -> reviewingListId = list.id
                            Constants.TESTING_LIST_NAME -> testingListId = list.id
                            Constants.READY_LIST_NAME -> readyListId = list.id
                            Constants.IMPEDIMENTS_LIST_NAME -> impedimentsListId = list.id
                            else -> {
                                if (list.name.toLowerCase().contains(Constants.DONE_LIST_NAME)) {
                                    potentialDoneLists.add(list)
                                }
                            }
                        }
                    }

                    var listWithHighestNumber : List? = null
                    var numberOfHighestList = 0

                    val regex = Regex("""([0-9])\w+""")
                    for (list in potentialDoneLists) {
                        val result = regex.find(list.name)
                        if (result != null) {
                            val currentListNumber = result.value.toInt()
                            if (currentListNumber > numberOfHighestList) {
                                listWithHighestNumber = list
                                numberOfHighestList = currentListNumber
                            }
                        }
                    }

                    if (listWithHighestNumber != null) {
                        doneListId = listWithHighestNumber.id
                    }

                    val sprintLists =
                        SprintLists(doneListId, doingListId, testingListId, reviewingListId)

                    RequestExecuter.execute(
                        SyncMembers(
                            board.id,
                            config.key,
                            config.token
                        )
                    )

                    RequestExecuter.execute(
                        SyncBurndownChartInfo(
                            board.id,
                            config.key,
                            config.token,
                            board.doneListId,
                            stringToday
                        )
                    )

                    RequestExecuter.execute(
                        SyncTeamStatistics(
                            board.id,
                            config.key,
                            config.token,
                            stringToday,
                            sprintLists
                        )
                    )

                    executeSyncCardsOfList(board, config, lists, stringToday)
                }
            }
            scheduleNewTaskForTomorrow(scheduler, zonedDateTime)
        }
    }

    private suspend fun executeSyncCardsOfList(
        board: BoardSyncConfig,
        config: SyncConfig,
        lists: Array<List>,
        stringToday: String
    ) {
        RequestExecuter.execute(
            SyncCardsOfLists(
                board.id,
                config.key,
                config.token,
                lists,
                stringToday
            )
        )
    }

    private fun scheduleNewTaskForTomorrow(scheduler: ScheduledExecutorService, currentDateTime: ZonedDateTime) {
        logger.info("Scheduling new sync timer task")
        TimeHelper.scheduleNewTaskForTheNextDay(
            SyncTimerTask(scheduler, currentDateTime),
            scheduler,
            currentDateTime
        )
    }
}