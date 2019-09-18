package nl.teqplay.trelloextension

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.service.sync.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.sync.SyncMembers
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SyncTimerTask(private val timer: Timer, private val calendar: Calendar) : TimerTask() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing sync timer task")
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
                    RequestExecuter.execute(
                        SyncMembers(
                            board.id,
                            config.key,
                            config.token
                        )
                    )

                    RequestExecuter.execute(SyncBurndownChartInfo(board.id, config.key, config.token, board.doneListId, stringToday))
                }
            }
        }
        scheduleNewTaskForTomorrow(timer, calendar)
    }

    private fun scheduleNewTaskForTomorrow(timer: Timer, calendar: Calendar) {
        logger.info("Scheduling new sync timer task")
        calendar.add(Calendar.DATE, 1)
        timer.schedule(SyncTimerTask(timer, calendar), calendar.time)
    }
}