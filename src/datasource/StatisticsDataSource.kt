package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.model.TeamStatistics
import org.litote.kmongo.*
import kotlin.collections.toList

object StatisticsDataSource {

    fun findAllTeamStatistics(
        boardId: String,
        sprintDates: SprintDates,
        database: Database.Companion.DatabaseImpl
    ): List<TeamStatistics> {
        val collection = database.teamStatisticsCollection
        return collection.find(
            and(
                TeamStatistics::boardId eq boardId,
                TeamStatistics::date gte sprintDates.epochStartDate,
                TeamStatistics::date lte sprintDates.epochEndDate
            )).toList()
    }

    fun saveTeamStatistics(item: TeamStatistics, database: Database.Companion.DatabaseImpl) {
        val collection = database.teamStatisticsCollection
        val result = collection.updateOne(
            and(
                TeamStatistics::boardId eq item.boardId,
                TeamStatistics::date eq item.date
            ), item)
        if (result.matchedCount != 1L) {
            collection.insertOne(item)
        }
    }
}