package nl.teqplay.trelloextension.service.sync

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.BurndownChartDataSource
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.service.BaseTrelloRequest
import nl.teqplay.trelloextension.service.burndownchart.DayProcessor
import java.sql.Date
import java.time.LocalDate

class SyncBurndownChartInfo(
    private val boardId: String,
    private val key: String,
    private val token: String,
    private val doneListId: String,
    private val today: String
) : BaseTrelloRequest<String>() {
    private val boardCall = TrelloCall(key, token)
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/$boardId/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        DayProcessor().run {
            val details = process(key, token, gson, boardCall, client, doneListId)
            convertToBurndownChartItem(details, todayDate)
        }.also {
            BurndownChartDataSource.updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(it, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}