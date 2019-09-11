package nl.teqplay.trelloextension.service.burndownchart

import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BurndownChart
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetBurndownChartInfo(
    val requestInfo: RequestInfo,
    private val sprintDates: SprintDates
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

        val databaseItems = BurndownChartDataSource.findAllBetweenEpochDates(sprintDates.epochStartDate, sprintDates.epochEndDate, db)

        databaseItems.forEach {
            burndownChart.items[it.date] = it
        }.also { burndownChart.items.toSortedMap() }

        return burndownChart
    }
}