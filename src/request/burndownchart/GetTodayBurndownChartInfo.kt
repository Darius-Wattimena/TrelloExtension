package nl.teqplay.trelloextension.request.burndownchart

import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import org.litote.kmongo.eq
import trello.model.BurndownChartItem
import java.sql.Date
import java.time.LocalDate

class GetTodayBurndownChartInfo(
    val requestInfo: RequestInfo,
    private val doneListId: String,
    private val today: String
) : BaseTrelloRequest<BurndownChartItem>() {
    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
    private var bcDetails = BurndownChartDetails()
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): BurndownChartItem {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        val processor = DayProcessor(requestInfo, doneListId)
        bcDetails = processor.process(requestInfo, gson, boardCall, client)
        val todayItem = processor.convertToBurndownChartItem(bcDetails, todayDate)
        db.saveWhen(todayItem, BurndownChartItem::class.java, BurndownChartItem::date eq todayDate)
        return todayItem
    }
}