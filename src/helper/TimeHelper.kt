package nl.teqplay.trelloextension.helper

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeHelper {
    fun getEpochMillisecondsFromISOLocalDate(isoDate: String, zoneId: String = "UTC"): Long {
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

    fun getMongoDBTimestamp(date: Date): String {
        return (date.time / 1000).toString(16) + "0000000000000000"
    }

}