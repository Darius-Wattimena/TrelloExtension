package nl.teqplay.request.burndownchart

import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.mongodb.Database
import nl.teqplay.request.BaseTrelloRequest
import org.litote.kmongo.eq
import trello.model.BurndownChartItem
import java.sql.Date
import java.time.LocalDate

class GetTodayBurndownChartInfo(
    val request: Request,
    private val doneListId: String,
    private val today: String
) : BaseTrelloRequest<BurndownChartItem>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private var bcDetails = BurndownChartDetails()
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/${request.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): BurndownChartItem {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        val processor = DayProcessor(request, doneListId)
        bcDetails = processor.process(request, gson, boardCall, client)
        val todayItem = processor.convertToBurndownChartItem(bcDetails, todayDate)
        db.saveWhen(todayItem, BurndownChartItem::class.java, BurndownChartItem::date eq todayDate)
        return todayItem
    }
}