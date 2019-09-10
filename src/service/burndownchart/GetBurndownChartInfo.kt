package nl.teqplay.trelloextension.service.burndownchart

import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BurndownChart
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.BaseTrelloRequest
import java.sql.Date
import java.time.LocalDate

class GetBurndownChartInfo(
    val requestInfo: RequestInfo,
    private val doneListId: String,
    private val sprintDates: SprintDates,
    private val today: String
) : BaseTrelloRequest<BurndownChart>() {

    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): BurndownChart {
        val burndownChart = BurndownChart(HashMap(), sprintDates.epochStartDate, sprintDates.epochEndDate)
        var databaseDays: Int

        if (today != "null") {
            val today = LocalDate.parse(today)
            val todayDate = Date.valueOf(today).time
            databaseDays = (today.dayOfYear - sprintDates.startLocalDate.dayOfYear) + 1

            val item = DayProcessor().run {
                val details = process(requestInfo, gson, boardCall, client, doneListId)
                convertToBurndownChartItem(details, todayDate)
            }

            burndownChart.items[todayDate] = item
            BurndownChartDataSource.updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(item, db)
        } else {
            databaseDays = (sprintDates.endLocalDate.dayOfYear - sprintDates.startLocalDate.dayOfYear) + 1
        }

        var checkingDatabaseDay = sprintDates.startLocalDate.plusDays((databaseDays - 1).toLong())

        while (databaseDays > 0) {
            val databaseDayEpoch = Date.valueOf(checkingDatabaseDay).time
            val bcDatabaseItem = BurndownChartDataSource.findWithEpochDate(databaseDayEpoch, db)
            if (bcDatabaseItem != null) {
                burndownChart.items[databaseDayEpoch] = bcDatabaseItem
            }
            checkingDatabaseDay = checkingDatabaseDay.minusDays(1)
            databaseDays--
        }

        burndownChart.items.toSortedMap()
        return burndownChart
    }
}