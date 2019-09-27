package nl.teqplay.trelloextension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.BoardHelper
import nl.teqplay.trelloextension.helper.TimeHelper.getISODateForToday
import nl.teqplay.trelloextension.model.sync.BoardSyncConfig
import nl.teqplay.trelloextension.model.sync.SyncConfig
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.service.card.SyncCardsOfLists
import nl.teqplay.trelloextension.service.list.GetBoardLists
import nl.teqplay.trelloextension.service.sync.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.sync.SyncMembers
import nl.teqplay.trelloextension.service.sync.SyncTeamStatistics
import org.slf4j.LoggerFactory
import java.util.*


class SyncTimerTask : TimerTask() {
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

                    val boardLists = BoardHelper.createBoardLists(lists)

                    RequestExecutor.execute(
                        SyncMembers(
                            board.id,
                            config.key,
                            config.token
                        )
                    )

                    RequestExecutor.execute(
                        SyncBurndownChartInfo(
                            board.id,
                            config.key,
                            config.token,
                            boardLists.DoneListId,
                            boardLists.ReadyListId,
                            stringToday
                        )
                    )

                    RequestExecutor.execute(
                        SyncTeamStatistics(
                            board.id,
                            config.key,
                            config.token,
                            stringToday,
                            boardLists
                        )
                    )

                    executeSyncCardsOfList(board, config, lists, stringToday)
                }
            }
        }
    }

    private suspend fun executeSyncCardsOfList(
        board: BoardSyncConfig,
        config: SyncConfig,
        lists: Array<List>,
        stringToday: String
    ) {
        RequestExecutor.execute(
            SyncCardsOfLists(
                board.id,
                config.key,
                config.token,
                lists,
                stringToday
            )
        )
    }
}