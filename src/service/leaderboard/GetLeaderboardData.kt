package nl.teqplay.trelloextension.service.leaderboard

import nl.teqplay.trelloextension.model.Leaderboard
import nl.teqplay.trelloextension.model.LeaderboardItem
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.LeaderboardDataSource
import nl.teqplay.trelloextension.service.BaseTrelloRequest
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

        val result = Leaderboard(
            emptyList<LeaderboardItem>().toMutableList(),
            startDate,
            endDate
        )

        //TODO when no board has been given return the leaderboard of the whole organisation
        if (id.isNotBlank()) {
            LeaderboardDataSource.findAllBoardItems(id, db).map {
                result.items.add(it)
            }
        }

        return result
    }

}