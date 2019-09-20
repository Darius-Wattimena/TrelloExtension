package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.TeamStatistics
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.updateOne

object StatisticsDataSource {

    fun findAllTeamStatistics(
        boardId: String,
        today: Long,
        lastWorkDay: Long,
        database: Database.Companion.DatabaseImpl
    ): List<TeamStatistics> {
        val collection = database.teamStatisticsCollection
        return collection.find(
            and(
                TeamStatistics::boardId eq boardId,
                or(
                    TeamStatistics::date eq today,
                    TeamStatistics::date eq lastWorkDay
                )
            )
        ).toList()
    }

    fun saveTeamStatistics(item: TeamStatistics, database: Database.Companion.DatabaseImpl) {
        val collection = database.teamStatisticsCollection
        val result = collection.updateOne(
            and(
                TeamStatistics::boardId eq item.boardId,
                TeamStatistics::date eq item.date
            ), item
        )
        if (result.matchedCount != 1L) {
            collection.insertOne(item)
        }
    }
}