package nl.teqplay.trelloextension.service.statistic

import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.StatisticsDataSource
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.service.BaseTrelloRequest
import java.time.ZoneId
import java.util.*

class GetTeamStatistics(private val boardId: String, val today: String) : BaseTrelloRequest<List<TeamStatistics>>() {
    private val db = Database.instance

    override fun prepare() {

    }

    private fun getLastWorkingDay(currentEpoch: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC"))).also {
            it.timeInMillis = currentEpoch
            it.set(Calendar.HOUR, 0)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
            it.set(Calendar.MILLISECOND, 0)
        }
        var dayOfWeek: Int
        do {
            cal.add(Calendar.DAY_OF_MONTH, -1)
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        } while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
        return cal.time.time
    }

    override suspend fun execute(): List<TeamStatistics> {
        val todayZonedDateTime = TimeHelper.getZonedDateTimeFromISOLocalDateString(today)
        val todayEpochMilliseconds = TimeHelper.epochSecondsToMilliseconds(todayZonedDateTime.toEpochSecond())

        val lastWorkingDayEpochMilliseconds = getLastWorkingDay(todayEpochMilliseconds)

        return StatisticsDataSource.findAllTeamStatistics(
            boardId,
            todayEpochMilliseconds,
            lastWorkingDayEpochMilliseconds,
            db
        )
    }
}