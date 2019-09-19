package nl.teqplay.trelloextension.helper

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

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
}