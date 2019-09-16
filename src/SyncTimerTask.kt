package nl.teqplay.trelloextension

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.service.sync.SyncMembers
import org.slf4j.LoggerFactory
import java.util.*

class SyncTimerTask(private val timer: Timer, private val calendar: Calendar) : TimerTask() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing sync timer task")
        GlobalScope.launch {
            val config = ConfigDataSource.getSyncConfig(Database.instance)
            if (config != null) {
                for (board in config.boards) {
                    RequestExecuter.execute(
                        SyncMembers(
                            board.id,
                            config.key,
                            config.token
                        )
                    )
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