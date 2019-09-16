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
    val requestInfo: RequestInfo,
    private val doneListId: String,
    private val today: String
) : BaseTrelloRequest<String>() {
    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        DayProcessor().run {
            val details = process(requestInfo, gson, boardCall, client, doneListId)
            convertToBurndownChartItem(details, todayDate)
        }.also {
            BurndownChartDataSource.updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(it, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}