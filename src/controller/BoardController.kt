package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.badRequest
import de.nielsfalk.ktor.swagger.get
import de.nielsfalk.ktor.swagger.ok
import de.nielsfalk.ktor.swagger.responds
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.locations.Location
import io.ktor.response.respondText
import io.ktor.routing.Routing
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.*
import nl.teqplay.trelloextension.service.board.GetBoard
import nl.teqplay.trelloextension.service.board.GetBoardStatistics
import nl.teqplay.trelloextension.service.board.GetDetailedBoard
import nl.teqplay.trelloextension.service.board.GetLastBoardAction
import nl.teqplay.trelloextension.service.burndownchart.GetBurndownChartInfo
import nl.teqplay.trelloextension.service.card.SyncTestingCards
import nl.teqplay.trelloextension.service.leaderboard.GetLeaderboardData
import nl.teqplay.trelloextension.service.member.GetBoardMembers
import nl.teqplay.trelloextension.service.slack.SendStuckTestingCardsToSlack
import nl.teqplay.trelloextension.service.statistic.GetTeamStatistics

data class Model<T>(val items: MutableList<T>)

@Group("Board operations")
@Location("/board/{id}")
data class board(val id: String, val key: String, val token: String)

@Group("Board operations")
@Location("/board/{id}/detailed")
data class boardDetailed(val id: String, val key: String, val token: String)

@Group("Board operations")
@Location("/board/{id}/statistics")
data class boardStatistics(val id: String, val key: String, val token: String)

@Group("Board operations")
@Location("/board/{id}/lastAction")
data class boardLastAction(val id: String, val key: String, val token: String)

@Group("Board operations")
@Location("/board/{id}/burndownchartinfo")
data class boardBurndownchart(val id: String, val startDate: String, val endDate: String)

@Group("Board operations")
@Location("/board/{id}/leaderboard")
data class boardLeaderboard(val id: String, val startDate: String, val endDate: String)

@Group("Board operations")
@Location("/board/{id}/members")
data class boardMembers(val id: String, val key: String, val token: String)

@Group("Board operations")
@Location("/board/{id}/teamstatistics")
data class boardTeamStatistics(val id: String, val today: String)

@Group("Board operations")
@Location("/board/{id}/sync/card")
data class sync(val id: String, val key: String, val token: String, val testingListId: String)

fun Routing.boardRouting() {
    authenticate("basicAuth") {
        get<sync>("temp".responds(ok<String>())) { board ->
            call.respondText(
                RequestExecuter.execute(SyncTestingCards(board.id, board.key, board.token, board.testingListId)),
                contentType = ContentType.Application.Json
            )
        }

        get<board>("Find a board".responds(ok<Board>(), badRequest())) { board ->
            val request = RequestInfo(board.id, board.key, board.token)
            call.respondText(
                RequestExecuter.execute(GetBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardDetailed>("Find a board detailed".responds(ok<Board>(), badRequest())) { board ->
            val request = RequestInfo(board.id, board.key, board.token)
            call.respondText(
                RequestExecuter.execute(GetDetailedBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardStatistics>("Find statistics of a board".responds(ok<Statistics>(), badRequest())) { board ->
            val request = RequestInfo(board.id, board.key, board.token)
            call.respondText(
                RequestExecuter.execute(GetBoardStatistics(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardLastAction>("Find the last action on a board".responds(ok<Action>(), badRequest())) { board ->
            val request = RequestInfo(board.id, board.key, board.token)
            call.respondText(
                RequestExecuter.execute(GetLastBoardAction(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardTeamStatistics>(
            "Find team statistics of a board".responds(
                ok<Model<TeamStatistics>>(),
                badRequest()
            )
        ) { board ->
            call.respondText(
                RequestExecuter.execute(GetTeamStatistics(board.id, board.today)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardBurndownchart>(
            "Get all the info for a burndownchart".responds(
                ok("Burndownchart"),
                badRequest()
            )
        ) { board ->
            val sprintDates = SprintDates(board.startDate, board.endDate)
            call.respondText(
                RequestExecuter.execute(GetBurndownChartInfo(board.id, sprintDates)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardLeaderboard>(
            "Get a list of all an item for all members with there leaderboard points".responds(
                ok<Leaderboard>(),
                badRequest()
            )
        ) { board ->
            call.respondText(
                RequestExecuter.execute(
                    GetLeaderboardData(
                        board.id,
                        board.startDate,
                        board.endDate
                    )
                ),
                contentType = ContentType.Application.Json
            )
        }

        get<boardMembers>("Find all the members of a board".responds(ok<Array<Member>>(), badRequest())) { board ->
            val request = RequestInfo(board.id, board.key, board.token)
            call.respondText(
                RequestExecuter.execute(GetBoardMembers(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}