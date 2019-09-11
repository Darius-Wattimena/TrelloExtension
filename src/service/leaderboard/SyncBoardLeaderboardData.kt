package nl.teqplay.trelloextension.service.leaderboard

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.model.LeaderboardItem
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.LeaderboardDataSource
import nl.teqplay.trelloextension.datasource.MemberDataSource
import nl.teqplay.trelloextension.service.BaseTrelloRequest
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.util.idValue
import java.sql.Date
import java.time.LocalDate

class SyncBoardLeaderboardData(
    val requestInfo: RequestInfo,
    private val doneListId: String,
    private val doingListId: String,
    private val testingListId: String,
    private val reviewingListId: String,
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
        val databaseLeaderboardItems = LeaderboardDataSource.findAllBoardItems(requestInfo.id, startDate, endDate, db)

        val databaseMembers = MemberDataSource.findAll(db)
        val resultItems = HashMap<String, LeaderboardItem>()

        databaseLeaderboardItems.map {
            it.assignedTasks = 0
            it.doingTasks = 0
            it.doneTasks = 0
            it.testingTasks = 0
            it.reviewingTasks = 0
        }.associateBy { LeaderboardItem::memberId to it }

        databaseMembers.forEach {member ->
            if (!resultItems.containsKey(member.id)) {
                resultItems[member.id] = LeaderboardItem(
                    requestInfo.id,
                    member.id,
                    member.fullName,
                    member.avatarUrl,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0L,
                    0L
                )
            }
        }

        lists.forEach { list ->
            val listCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
            listCall.request = "/lists/${list.id}/cards"
            listCall.parameters["fields"] = "id,name"
            listCall.parameters["members"] = "true"
            listCall.parameters["member_field"] = "id"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            for (card in cards) {
                for (cardMember in card.members) {
                    val leaderBoardItem = resultItems[cardMember.id]
                    if (leaderBoardItem != null) {
                        when (list.id) {
                            doneListId -> leaderBoardItem.doneTasks++
                            doingListId -> leaderBoardItem.doingTasks++
                            testingListId -> leaderBoardItem.testingTasks++
                            reviewingListId -> leaderBoardItem.reviewingTasks++
                        }
                        leaderBoardItem.assignedTasks++
                    }
                }
            }
        }

        for (item in resultItems) {
            item.value.startDate = startDate
            item.value.endDate = endDate

            LeaderboardDataSource.updateWhenBoardAndMemberIdIsFoundOtherwiseInsert(item.value, requestInfo.id, item.key, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }

}