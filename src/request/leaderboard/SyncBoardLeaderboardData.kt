package nl.teqplay.trelloextension.request.leaderboard

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Card
import nl.teqplay.trelloextension.trello.model.LeaderboardItem
import nl.teqplay.trelloextension.trello.model.List
import nl.teqplay.trelloextension.trello.model.Member
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.sql.Date
import java.time.LocalDate

class SyncBoardLeaderboardData(
    val requestInfo: RequestInfo,
    private val doneListId: String,
    private val doingListId: String,
    private val testingListId: String,
    private val startDate: String,
    private val endDate: String
) : BaseTrelloRequest<String>() {
    private val db = Database.instance
    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        boardCall.request = "/boards/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val startOfSprint = LocalDate.parse(startDate)
        val endOfSprint = LocalDate.parse(endDate)
        val startDate = Date.valueOf(startOfSprint).time
        val endDate = Date.valueOf(endOfSprint).time

        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)
        val databaseLeaderboardItems = db.findAll(and(
            LeaderboardItem::boardId eq requestInfo.id,
            LeaderboardItem::startDate eq startDate,
            LeaderboardItem::endDate eq endDate
        ), LeaderboardItem::class.java)
        val databaseMembers = db.findAll(Member::class.java)
        val resultItems = HashMap<String, LeaderboardItem>()

        if (databaseLeaderboardItems != null) {
            for (databaseLeaderboardItem in databaseLeaderboardItems) {
                databaseLeaderboardItem.assignedTasks = 0
                databaseLeaderboardItem.doingTasks = 0
                databaseLeaderboardItem.doneTasks = 0
                databaseLeaderboardItem.testingTasks = 0
                resultItems[databaseLeaderboardItem.memberId] = databaseLeaderboardItem
            }
        }

        if (databaseMembers != null) {
            for (databaseMember in databaseMembers) {
                if (!resultItems.containsKey(databaseMember.id)) {
                    resultItems[databaseMember.id] = LeaderboardItem(
                        null,
                        requestInfo.id,
                        databaseMember.id,
                        databaseMember.fullName,
                        databaseMember.avatarUrl,
                        0,
                        0,
                        0,
                        0,
                        0L,
                        0L)
                }
            }
        }

        for (list in lists) {
            val listCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
            listCall.request = "/lists/${list.id}/cards"
            listCall.parameters["fields"] = "id,name"
            listCall.parameters["members"] = "true"
            listCall.parameters["member_field"] = "id"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            for (card in cards) {
                for(cardMember in card.members) {
                    val leaderBoardItem = resultItems[cardMember.id]
                    if (leaderBoardItem != null) {
                        when (list.id) {
                            doneListId -> leaderBoardItem.doneTasks++
                            doingListId -> leaderBoardItem.doingTasks++
                            testingListId -> leaderBoardItem.testingTasks++
                        }
                        leaderBoardItem.assignedTasks++
                    }
                }
            }
        }

        for (item in resultItems) {
            item.value.startDate = startDate
            item.value.endDate = endDate

            db.saveWhen(item.value, LeaderboardItem::class.java, and(
                LeaderboardItem::boardId eq requestInfo.id,
                LeaderboardItem::memberId eq item.key))
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }

}