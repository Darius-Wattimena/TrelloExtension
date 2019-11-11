package nl.teqplay.trelloextension.service.burndownchart

import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.BoardHelper
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BurndownChart
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.BaseRequest
import nl.teqplay.trelloextension.service.list.GetBoardLists

class GetBurndownChartInfo(private val boardId: String, private val sprintDates: SprintDates) :
    BaseRequest<BurndownChart>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): BurndownChart {
        val burndownChart = BurndownChart(mutableListOf(), sprintDates.epochStartDate, sprintDates.epochEndDate)

        val databaseItems =
            BurndownChartDataSource.findAllBetweenEpochDates(
                boardId,
                sprintDates.epochStartDate,
                sprintDates.epochEndDate,
                db
            )

        val sortedItems = databaseItems.sortedBy { it.date }
        burndownChart.items.addAll(sortedItems)

        val config = ConfigDataSource.getSyncConfig(Database.instance)

        if (config != null) {
            val currentDay = TimeHelper.getZonedDateTimeFromISOLocalDateString(TimeHelper.getISODateForToday())
                .plusDays(1)

            DayProcessor().run {

                val boardCall = TrelloCall(config.key, config.token)

                boardCall.request = "/board/$boardId/lists"
                boardCall.parameters["cards"] = "none"
                boardCall.parameters["fields"] = "none"

                val lists = GetBoardLists(
                    boardId,
                    config.key,
                    config.token
                ).execute()

                val boardLists = BoardHelper.createBoardLists(lists)

                val details = process(config.key, config.token, gson, boardCall, client, boardLists.DoneListId, boardLists.ReadyListId)
                convertToBurndownChartItem(boardId, details, currentDay.toEpochSecond() * 1000)
            }.also {
                it.changeableDate = true
                burndownChart.items.add(it)
            }
        }

        return burndownChart
    }
}