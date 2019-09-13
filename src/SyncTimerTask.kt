package nl.teqplay.trelloextension

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.teqplay.trelloextension.service.member.SyncMembers
import org.slf4j.LoggerFactory
import java.util.*

class SyncTimerTask(private val timer: Timer, private val calendar: Calendar) : TimerTask() {
    private val API_KEY = "62f0297bf821e374ae28a1fbab5ef9fb"
    private val OAUTH_TOKEN = "dd1d9766b7c5be875fe3e73c590bf3d53237f383b719311182c1e76ba9ce0da5"
    private val BOARD_ID = "qDAFPals"
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Executing sync timer task")
        GlobalScope.launch {
            RequestExecuter.execute(SyncMembers(BOARD_ID, API_KEY, OAUTH_TOKEN))
        }
        scheduleNewTaskForTomorrow(timer, calendar)
    }

    private fun scheduleNewTaskForTomorrow(timer: Timer, calendar: Calendar) {
        logger.info("Scheduling new sync timer task")
        calendar.add(Calendar.DATE, 1)
        timer.schedule(SyncTimerTask(timer, calendar), calendar.time)
    }
}