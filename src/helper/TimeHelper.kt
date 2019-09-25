package nl.teqplay.trelloextension.helper

import java.text.SimpleDateFormat
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

    fun getISODateForToday(): String {
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC")).also {
            it.set(Calendar.HOUR_OF_DAY, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
        }

        val convertedToday = ZonedDateTime.ofInstant(today.toInstant(), ZoneId.of("UTC"))

        return DateTimeFormatter.ISO_LOCAL_DATE.format(convertedToday)
    }

    fun getYesterday(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return cal.time
    }

    fun getISO8061UTCFromDate(date: Date): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        df.timeZone = tz
        return df.format(date)
    }

    fun getMongoDBTimestamp(date: Date): String {
        return (date.time / 1000).toString(16) + "0000000000000000"
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