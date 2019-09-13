package nl.teqplay.trelloextension

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import java.util.*

class CustomTimerFeature(configuration: Configuration) {
    val timer = configuration.timer
    val calendar = configuration.calendar

    class Configuration {
        var timer : Timer? = null
        var calendar : Calendar? = null
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, CustomTimerFeature> {
        override val key = AttributeKey<CustomTimerFeature>("TimerFeature")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomTimerFeature {
            val configuration = Configuration().apply(configure)

            val feature = CustomTimerFeature(configuration)

            pipeline.intercept(ApplicationCallPipeline.Features) {
                feature.prepareTimer(feature.timer, feature.calendar)
            }

            return feature
        }
    }

    private fun setupTimer(timer: Timer, calendar: Calendar) {
        timer.schedule(SyncTimerTask(timer, calendar), calendar.time)
    }

    private fun prepareTimer(timer: Timer?, calendar: Calendar?) {
        if (timer != null && calendar != null) {
            setupTimer(timer, calendar)
        }
    }


}