package nl.teqplay.trelloextension

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.BoardHelper
import nl.teqplay.trelloextension.service.slack.SendStuckTestingCardsToSlack
import org.slf4j.LoggerFactory
import java.util.*

class SlackDailyMessageTimerTask : TimerTask() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing slack message timer task")
        CoroutineScope(Dispatchers.IO).launch {
            val syncConfig = ConfigDataSource.getSyncConfig(Database.instance)
            if (syncConfig != null) {
                for (board in syncConfig.boards) {
                    val boardLists = BoardHelper.getBoardLists(board.id, syncConfig.key, syncConfig.token)
                    val config = HoconApplicationConfig(ConfigFactory.load())
                    val databaseConfig = config.config("ktor.application")
                    val slackToken = databaseConfig.property("slack_token").getString()

                    RequestExecutor.execute(
                        SendStuckTestingCardsToSlack(
                            slackToken,
                            board.id,
                            boardLists.TestingListId,
                            3
                        )
                    )
                }
            }
        }
    }
}