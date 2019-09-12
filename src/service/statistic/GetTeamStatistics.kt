package nl.teqplay.trelloextension.service.statistic

import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.StatisticsDataSource
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetTeamStatistics(private val boardId: String, private val sprintDates: SprintDates) : BaseTrelloRequest<List<TeamStatistics>>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): List<TeamStatistics> {
        return StatisticsDataSource.findAllTeamStatistics(boardId, sprintDates, db)
    }
}