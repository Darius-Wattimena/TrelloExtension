package nl.teqplay.trelloextension.request.leaderboard

import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Leaderboard
import nl.teqplay.trelloextension.trello.model.LeaderboardItem
import org.litote.kmongo.eq
import java.sql.Date
import java.time.LocalDate

class GetLeaderboardData(
    private val id: String,
    private val startDate: String,
    private val endDate: String
) : BaseTrelloRequest<Leaderboard>() {

    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): Leaderboard {
        val startOfSprint = LocalDate.parse(startDate)
        val endOfSprint = LocalDate.parse(endDate)
        val startDate = Date.valueOf(startOfSprint).time
        val endDate = Date.valueOf(endOfSprint).time

        val result = Leaderboard(emptyList<LeaderboardItem>().toMutableList(), startDate, endDate)

        //TODO when no board has been given return the leaderboard of the whole organisation
        if (id.isNotBlank()) {
            val leaderboardItems = db.findAll(LeaderboardItem::boardId eq id, LeaderboardItem::class.java)

            if (leaderboardItems != null) {
                for (leaderboardItem in leaderboardItems) {
                    result.items.add(leaderboardItem)
                }
            }
        }

        return result
    }

}