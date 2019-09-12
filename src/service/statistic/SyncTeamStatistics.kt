package nl.teqplay.trelloextension.service.statistic

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.StatisticsDataSource
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.model.SprintLists
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.service.BaseTrelloRequest
import java.sql.Date
import java.time.LocalDate

class SyncTeamStatistics(private val requestInfo: RequestInfo, private val today: String, private val sprintLists: SprintLists) : BaseTrelloRequest<String>() {
    private val db = Database.instance
    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        boardCall.request = "/boards/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "all"
        boardCall.parameters["card_fields"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)
        val resultItem = TeamStatistics(requestInfo.id, todayDate)

        for (list in lists) {
            when (list.id) {
                sprintLists.doingListId -> resultItem.totalDoing += list.cards.count()
                sprintLists.doneListId -> resultItem.totalDone += list.cards.count()
                sprintLists.reviewingListId -> resultItem.totalReviewing += list.cards.count()
                sprintLists.testingListId -> resultItem.totalTesting += list.cards.count()
            }
        }

        StatisticsDataSource.saveTeamStatistics(resultItem, db)

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}