package nl.teqplay.trelloextension.service.leaderboard

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.LeaderboardDataSource
import nl.teqplay.trelloextension.datasource.MemberDataSource
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.*
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class SyncBoardLeaderboardData(
    val requestInfo: RequestInfo,
    private val leaderboardLists: LeaderboardLists,
    private val sprintDates: SprintDates
) : BaseTrelloRequest<String>() {
    private val db = Database.instance
    private val boardCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        boardCall.request = "/boards/${requestInfo.id}/lists"
        boardCall.parameters["cards"] = "none"
        boardCall.parameters["fields"] = "none"
    }

    override suspend fun execute(): String {
        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)
        val databaseLeaderboardItems = LeaderboardDataSource.findAllBoardItems(requestInfo.id, sprintDates.epochStartDate, sprintDates.epochEndDate, db)

        val databaseMembers = MemberDataSource.findAll(db)
        val resultItems = HashMap<String, LeaderboardItem>()

        resetDatabaseLeaderboardItems(databaseLeaderboardItems)
        insertMissingMembersInResultMap(databaseMembers, requestInfo.id, resultItems)

        lists.forEach { list ->
            val cards = getAllCardsOfAList(list.id, requestInfo)
            processLeaderboardDataFromCards(cards, list.id, leaderboardLists, resultItems)
        }

        resultItems.map {
            it.value.startDate = sprintDates.epochStartDate
            it.value.endDate = sprintDates.epochEndDate
            LeaderboardDataSource.updateWhenBoardAndMemberIdIsFoundOtherwiseInsert(it.value, requestInfo.id, it.key, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }

    private fun resetDatabaseLeaderboardItems(items: Collection<LeaderboardItem>) {
        items.map {
            it.assignedTasks = 0
            it.doingTasks = 0
            it.doneTasks = 0
            it.testingTasks = 0
            it.reviewingTasks = 0
        }
    }

    private fun insertMissingMembersInResultMap(members: Collection<Member>, boardId: String, resultItems: HashMap<String, LeaderboardItem>) {
        members.forEach {member ->
            if (!resultItems.containsKey(member.id)) {
                resultItems[member.id] = LeaderboardItem(
                    boardId,
                    member.id,
                    member.fullName,
                    member.avatarUrl
                )
            }
        }
    }

    private suspend fun getAllCardsOfAList(listId: String, requestInfo: RequestInfo): Array<Card> {
        val listCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
        listCall.request = "/lists/$listId/cards"
        listCall.parameters["fields"] = "id,name"
        listCall.parameters["members"] = "true"
        listCall.parameters["member_field"] = "id"

        return JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)
    }

    private fun processLeaderboardDataFromCards(cards: Array<Card>, listId: String, leaderboardLists: LeaderboardLists, resultItems: HashMap<String, LeaderboardItem>) {
        for (card in cards) {
            for (cardMember in card.members) {
                val leaderBoardItem = resultItems[cardMember.id]
                if (leaderBoardItem != null) {
                    when (listId) {
                        leaderboardLists.doneListId -> leaderBoardItem.doneTasks++
                        leaderboardLists.doingListId -> leaderBoardItem.doingTasks++
                        leaderboardLists.testingListId -> leaderBoardItem.testingTasks++
                        leaderboardLists.reviewingListId -> leaderBoardItem.reviewingTasks++
                    }
                    leaderBoardItem.assignedTasks++
                }
            }
        }
    }
}