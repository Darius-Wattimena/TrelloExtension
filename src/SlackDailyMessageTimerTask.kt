package nl.teqplay.trelloextension

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.service.slack.SendStuckTestingCardsToSlack
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ScheduledExecutorService

class SlackDailyMessageTimerTask(
    private val scheduler: ScheduledExecutorService,
    private val zonedDateTime: ZonedDateTime
) : TimerTask() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing sync timer task")
        CoroutineScope(Dispatchers.IO).launch {
            val config = ConfigDataSource.getSyncConfig(Database.instance)
            if (config != null) {
                for (board in config.boards) {
                    val config = HoconApplicationConfig(ConfigFactory.load())
                    val databaseConfig = config.config("ktor.application")
                    val slackToken = databaseConfig.property("slack_token").getString()

                    RequestExecuter.execute(
                        SendStuckTestingCardsToSlack(
                            slackToken,
                            board.id,
                            board.testingListId,
                            3
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
            SlackDailyMessageTimerTask(scheduler, currentDateTime),
            scheduler,
            currentDateTime,
            hour = 7
        )
    }
}