package nl.teqplay.trelloextension.service.sync

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.StatisticsDataSource
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BoardLists
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.service.BaseRequest

class SyncTeamStatistics(
    private val boardId: String,
    key: String,
    token: String,
    private val today: String,
    private val sprintLists: BoardLists
) : BaseRequest<String>() {
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
                sprintLists.DoingListId -> resultItem.totalDoing += list.cards.count()
                sprintLists.DoneListId -> resultItem.totalDone += list.cards.count()
                sprintLists.ReviewingListId -> resultItem.totalReviewing += list.cards.count()
                sprintLists.TestingListId -> resultItem.totalTesting += list.cards.count()
            }
        }

        StatisticsDataSource.saveTeamStatistics(resultItem, db)

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}