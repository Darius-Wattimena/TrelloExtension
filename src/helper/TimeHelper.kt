package nl.teqplay.trelloextension.helper

import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object TimeHelper {
    fun getISOLocalDateStringToEpochMilliseconds(isoDate: String, zoneId: String = "UTC"): Long {
        return epochSecondsToMilliseconds(
            getZonedDateTimeFromISOLocalDateString(isoDate, zoneId)
                .toEpochSecond()
        )
    }

    fun getZonedDateTimeFromISOLocalDateString(isoDate: String, zoneId: String = "UTC"): ZonedDateTime {
        return LocalDate.parse(isoDate)
            .atTime(0, 0)
            .atZone(ZoneId.of(zoneId))
    }

    fun epochSecondsToMilliseconds(epochSeconds: Long): Long {
        return epochSeconds * 1000;
    }

    fun getISODateForTimerTask(): String {
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).also {
            it.set(Calendar.HOUR_OF_DAY, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
        }

        val convertedToday = ZonedDateTime.ofInstant(today.toInstant(), ZoneId.of("UTC"))

        return DateTimeFormatter.ISO_LOCAL_DATE.format(convertedToday)
    }

    fun scheduleNewTaskForTheNextDay(
        task: TimerTask,
        scheduler: ScheduledExecutorService,
        currentDateTime: ZonedDateTime,
        hour: Int = 2,
        minute: Int = 0,
        second: Int = 0
    ) {
        val nextZonedDateTime = currentDateTime
            .withHour(hour)
            .withMinute(minute)
            .withSecond(second)
            .plusDays(1)

        val duration = Duration.between(currentDateTime, nextZonedDateTime)
        val initialDelay = duration.seconds

        scheduler.scheduleAtFixedRate(
            task,
            initialDelay,
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.SECONDS
        )
    }
}