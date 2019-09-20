package nl.teqplay.trelloextension

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CustomTimerFeature(configuration: Configuration) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    val scheduler = configuration.scheduler
    val zonedDateTime = configuration.zonedDateTime

    class Configuration {
        var scheduler: ScheduledExecutorService? = null
        var zonedDateTime: ZonedDateTime? = null
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, CustomTimerFeature> {
        override val key = AttributeKey<CustomTimerFeature>("TimerFeature")
        var timerPrepared = false

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): CustomTimerFeature {
            val configuration = Configuration().apply(configure)

            val feature = CustomTimerFeature(configuration)

            pipeline.intercept(ApplicationCallPipeline.Features) {
                if (!timerPrepared) {
                    feature.prepareTimer(feature.scheduler, feature.zonedDateTime)
                }
            }

            return feature
        }
    }

    private fun setupTimer(scheduler: ScheduledExecutorService, currentDateTime: ZonedDateTime) {
        var nextZonedDateTime = currentDateTime
            .withHour(2)
            .withMinute(0)
            .withSecond(0)
        if (currentDateTime > nextZonedDateTime)
            nextZonedDateTime = nextZonedDateTime.plusDays(1)

        val duration = Duration.between(currentDateTime, nextZonedDateTime)
        val initialDelay = duration.seconds

        logger.info("Setting up first timer task and run this task in $initialDelay seconds")

        scheduler.scheduleAtFixedRate(
            SyncTimerTask(scheduler, currentDateTime),
            initialDelay,
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.SECONDS
        )
    }

    private fun prepareTimer(scheduler: ScheduledExecutorService?, zonedDateTime: ZonedDateTime?) {
        if (scheduler != null && zonedDateTime != null) {
            setupTimer(scheduler, zonedDateTime)
        }
        timerPrepared = true
    }


}