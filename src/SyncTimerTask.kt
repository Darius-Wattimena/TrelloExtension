package nl.teqplay.trelloextension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TimeHelper.getISODateForTimerTask
import nl.teqplay.trelloextension.model.SprintLists
import nl.teqplay.trelloextension.service.card.SyncTestingCards
import nl.teqplay.trelloextension.service.sync.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.sync.SyncMembers
import nl.teqplay.trelloextension.service.sync.SyncTeamStatistics
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


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
                val stringToday = getISODateForTimerTask()

                for (board in config.boards) {
                    val sprintLists =
                        SprintLists(board.doneListId, board.doingListId, board.testingListId, board.reviewingListId)

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

                    RequestExecuter.execute(
                        SyncTestingCards(
                            board.id,
                            config.key,
                            config.token,
                            board.testingListId
                        )
                    )
                }
            }
            scheduleNewTaskForTomorrow(scheduler, zonedDateTime)
        }
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