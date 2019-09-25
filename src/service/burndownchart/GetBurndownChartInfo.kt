package nl.teqplay.trelloextension.service.burndownchart

import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.model.BurndownChart
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.BaseRequest

class GetBurndownChartInfo(private val boardId: String, private val sprintDates: SprintDates) : BaseRequest<BurndownChart>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): BurndownChart {
        val burndownChart = BurndownChart(mutableListOf(), sprintDates.epochStartDate, sprintDates.epochEndDate)

        val databaseItems =
            BurndownChartDataSource.findAllBetweenEpochDates(boardId, sprintDates.epochStartDate, sprintDates.epochEndDate, db)

        val sortedItems = databaseItems.sortedBy { it.date }

        burndownChart.items.addAll(sortedItems)

        return burndownChart
    }
}