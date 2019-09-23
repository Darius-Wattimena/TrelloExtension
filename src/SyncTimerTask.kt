package nl.teqplay.trelloextension

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.model.SprintLists
import nl.teqplay.trelloextension.service.card.SyncTestingCards
import nl.teqplay.trelloextension.service.slack.SendStuckTestingCardsToSlack
import nl.teqplay.trelloextension.service.sync.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.sync.SyncMembers
import nl.teqplay.trelloextension.service.sync.SyncTeamStatistics
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
        scheduleNewTaskForTomorrow(scheduler, zonedDateTime)
        GlobalScope.launch {
            val config = ConfigDataSource.getSyncConfig(Database.instance)
            if (config != null) {
                val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).also {
                    it.set(Calendar.HOUR_OF_DAY, 0)
                    it.set(Calendar.MINUTE, 0)
                    it.set(Calendar.SECOND, 0)
                }

                val convertedToday = ZonedDateTime.ofInstant(today.toInstant(), ZoneId.of("UTC"))

                val stringToday = DateTimeFormatter.ISO_LOCAL_DATE.format(convertedToday)

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


                    val config = HoconApplicationConfig(ConfigFactory.load())
                    val databaseConfig = config.config("ktor.application")
                    val slackToken = databaseConfig.property("slack_token").getString()

                    RequestExecuter.execute(
                        SendStuckTestingCardsToSlack(
                            slackToken,
                            "qDAFPals",
                            "5411bfbbc1a47d1d609a572b",
                            3
                        )
                    )
                }
            }
        }
    }

    private fun scheduleNewTaskForTomorrow(scheduler: ScheduledExecutorService, currentDateTime: ZonedDateTime) {
        logger.info("Scheduling new sync timer task")
        val nextZonedDateTime = currentDateTime.plusMinutes(1)
        val duration = Duration.between(currentDateTime, nextZonedDateTime)
        val initialDelay = duration.seconds

        scheduler.scheduleAtFixedRate(
            SyncTimerTask(scheduler, nextZonedDateTime),
            initialDelay,
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.SECONDS
        )
    }
}