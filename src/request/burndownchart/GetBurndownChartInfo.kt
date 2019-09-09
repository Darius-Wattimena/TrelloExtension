package nl.teqplay.trelloextension.request.burndownchart

import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.BurndownChart
import org.litote.kmongo.eq
import trello.model.BurndownChartItem
import java.sql.Date
import java.time.LocalDate

class GetBurndownChartInfo(
    val request: Request,
    private val doneListId: String,
    private val startDate: String,
    private val endDate: String,
    private val today: String
) : BaseTrelloRequest<BurndownChart>() {

    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private var bcDetails = BurndownChartDetails()
    private val db = Database.instance

    override fun prepare() {
        boardCall.request = "/board/${request.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): BurndownChart {
        val startOfSprint = LocalDate.parse(startDate)
        val endOfSprint = LocalDate.parse(endDate)
        val startDate = Date.valueOf(startOfSprint).time
        val endDate = Date.valueOf(endOfSprint).time

        val burndownChart = BurndownChart(HashMap(), startDate, endDate)
        var databaseDays = (endOfSprint.dayOfYear - startOfSprint.dayOfYear) + 1

        if (today != "null") {
            val today = LocalDate.parse(today)
            val todayDate = Date.valueOf(today).time
            databaseDays = (today.dayOfYear - startOfSprint.dayOfYear) + 1
            val processor = DayProcessor(request, doneListId)
            bcDetails = processor.process(request, gson, boardCall, client)
            val item = processor.convertToBurndownChartItem(bcDetails, todayDate)
            burndownChart.items[todayDate] = item
            db.saveWhen(item, BurndownChartItem::class.java, BurndownChartItem::date eq todayDate)
        }

        var checkingDatabaseDay = startOfSprint.plusDays((databaseDays - 1).toLong())

        while (databaseDays > 0) {
            val databaseDayEpoch = Date.valueOf(checkingDatabaseDay).time
            val bcDatabaseItem = db.find("date", databaseDayEpoch.toString(), BurndownChartItem::class.java)
            if (bcDatabaseItem != null) {
                burndownChart.items[databaseDayEpoch] = bcDatabaseItem
            }
            checkingDatabaseDay = checkingDatabaseDay.minusDays(1)
            databaseDays --
        }

        burndownChart.items = burndownChart.items.toSortedMap()
        return burndownChart
    }




}