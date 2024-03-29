package nl.teqplay.trelloextension.service.sync

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.service.BaseRequest
import nl.teqplay.trelloextension.service.burndownchart.DayProcessor

class SyncBurndownChartInfo(
    private val boardId: String,
    private val key: String,
    private val token: String,
    private val doneListId: String,
    private val readyListId: String,
    private val today: String
) : BaseRequest<String>() {
    private val boardCall = TrelloCall(key, token)
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/$boardId/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val todayDate = TimeHelper.getEpochMillisecondsFromISOLocalDate(today)

        DayProcessor().run {
            val details = process(key, token, gson, boardCall, client, doneListId, readyListId)
            convertToBurndownChartItem(boardId, details, todayDate)
        }.also {
            BurndownChartDataSource.updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(it, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}