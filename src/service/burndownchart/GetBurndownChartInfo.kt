package nl.teqplay.trelloextension.service.burndownchart

import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.model.BurndownChart
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetBurndownChartInfo(private val sprintDates: SprintDates) : BaseTrelloRequest<BurndownChart>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): BurndownChart {
        val burndownChart = BurndownChart(HashMap(), sprintDates.epochStartDate, sprintDates.epochEndDate)

        val databaseItems = BurndownChartDataSource.findAllBetweenEpochDates(sprintDates.epochStartDate, sprintDates.epochEndDate, db)

        databaseItems.forEach {
            burndownChart.items[it.date] = it
        }
        burndownChart.items.toSortedMap()
        return burndownChart
    }
}