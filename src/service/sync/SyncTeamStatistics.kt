package nl.teqplay.trelloextension.service.sync

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.StatisticsDataSource
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.model.SprintLists
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class SyncTeamStatistics(
    private val boardId: String,
    key: String,
    token: String,
    private val today: String,
    private val sprintLists: SprintLists
) : BaseTrelloRequest<String>() {
    private val db = Database.instance
    private val boardCall = TrelloCall(key, token)

    override fun prepare() {
        boardCall.request = "/boards/$boardId/lists"
        boardCall.parameters["cards"] = "all"
        boardCall.parameters["card_fields"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val todayDate = TimeHelper.getISOLocalDateStringToEpochMilliseconds(today)

        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)
        val resultItem = TeamStatistics(boardId, todayDate)

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