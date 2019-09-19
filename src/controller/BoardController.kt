package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.get
import de.nielsfalk.ktor.swagger.notFound
import de.nielsfalk.ktor.swagger.ok
import de.nielsfalk.ktor.swagger.responds
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.locations.Location
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.MissingParameterException
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.*
import nl.teqplay.trelloextension.service.action.GetAction
import nl.teqplay.trelloextension.service.board.GetBoard
import nl.teqplay.trelloextension.service.board.GetBoardStatistics
import nl.teqplay.trelloextension.service.board.GetDetailedBoard
import nl.teqplay.trelloextension.service.board.GetLastBoardAction
import nl.teqplay.trelloextension.service.burndownchart.GetBurndownChartInfo
import nl.teqplay.trelloextension.service.leaderboard.GetLeaderboardData
import nl.teqplay.trelloextension.service.member.GetBoardMembers
import nl.teqplay.trelloextension.service.statistic.GetTeamStatistics
import nl.teqplay.trelloextension.service.sync.SyncBoardLeaderboardData
import nl.teqplay.trelloextension.service.sync.SyncTeamStatistics

data class Model<T>(val items: MutableList<T>)

@Group("Board operations")
@Location("/board/{id}")
data class board(val id: String)

@Group("Board operations")
@Location("/board/{id}/detailed")
data class boardDetailed(val id: String)

@Group("Board operations")
@Location("/board/{id}/statistics")
data class boardStatistics(val id: String)

@Group("Board operations")
@Location("/board/{id}/lastAction")
data class boardLastAction(val id: String)

@Group("Board operations")
@Location("/board/{id}/burndownchartinfo")
data class boardBurndownchart(val id: String)

@Group("Board operations")
@Location("/board/{id}/leaderboard")
data class boardLeaderboard(val id: String)

@Group("Board operations")
@Location("/board/{id}/members")
data class boardMembers(val id: String)

@Group("Board operations")
@Location("/board/{id}/teamstatistics")
data class boardTeamStatistics(val id: String)

fun Routing.boardRouting() {
    authenticate("basicAuth") {
        get<board>("Find a board".responds(ok<Board>(), notFound())) { board->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, board.id)
            call.respondText(
                RequestExecuter.execute(GetBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardDetailed>("Find a board detailed".responds(ok<Board>(), notFound())) { board->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, board.id)
            call.respondText(
                RequestExecuter.execute(GetDetailedBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardStatistics>("Find statistics of a board".responds(ok<Statistics>(), notFound())) { board->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, board.id)
            call.respondText(
                RequestExecuter.execute(GetBoardStatistics(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardLastAction>("Find the last action on a board".responds(ok<Action>(), notFound())) { board->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, board.id)
            call.respondText(
                RequestExecuter.execute(GetLastBoardAction(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<boardTeamStatistics>("Find team statistics of a board".responds(ok<Model<TeamStatistics>>(), notFound())) { board->
            val queryParameters = call.request.queryParameters
            val today = queryParameters["today"]
            if (today == null) {
                throw MissingParameterException("You did not provide a today parameter")
            } else {
                call.respondText(
                    RequestExecuter.execute(GetTeamStatistics(board.id, today)),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }

    route("board/") {
        get("{id}/burndownchartinfo") {
            val queryParameters = call.request.queryParameters
            val startDate = queryParameters["startDate"]
            val endDate = queryParameters["endDate"]

            if (startDate == null || endDate == null) {
                throw MissingParameterException("You didn't provide a startDate or endDate value as query parameters")
            } else {
                val sprintDates = SprintDates(startDate, endDate)
                call.respondText(
                    RequestExecuter.execute(GetBurndownChartInfo(sprintDates)),
                    contentType = ContentType.Application.Json
                )
            }
        }

        get("{id}/sync/leaderboard") {
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, call.parameters["id"]!!)
            val doneListId = queryParameters["doneListId"]
            val doingListId = queryParameters["doingListId"]
            val testingListId = queryParameters["testingListId"]
            val reviewingListId = queryParameters["reviewingListId"]
            val startDate = queryParameters["startDate"]
            val endDate = queryParameters["endDate"]
            if (doneListId == null || doingListId == null || testingListId == null || reviewingListId == null) {
                throw MissingParameterException("You didn't provide all the 4 different list ids as query parameters")
            } else if (startDate == null || endDate == null) {
                throw MissingParameterException("You didn't provide a startDate or endDate value as query parameters")
            } else {
                val sprintDates = SprintDates(startDate, endDate)
                val leaderboardLists = SprintLists(
                    doneListId,
                    doingListId,
                    testingListId,
                    reviewingListId
                )
                call.respondText(
                    RequestExecuter.execute(
                        SyncBoardLeaderboardData(
                            request,
                            leaderboardLists,
                            sprintDates
                        )
                    ),
                    contentType = ContentType.Application.Json
                )
            }
        }

        get("{id}/leaderboard") {
            val queryParameters = call.request.queryParameters
            val startDate = queryParameters["startDate"]
            val endDate = queryParameters["endDate"]
            call.respondText(
                RequestExecuter.execute(
                    GetLeaderboardData(
                        call.parameters["id"]!!,
                        startDate.toString(),
                        endDate.toString()
                    )
                ),
                contentType = ContentType.Application.Json
            )
        }



        get("{id}/members") {
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetBoardMembers(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/sync/teamstatistics") {
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, call.parameters["id"]!!)
            val doneListId = queryParameters["doneListId"]
            val doingListId = queryParameters["doingListId"]
            val testingListId = queryParameters["testingListId"]
            val reviewingListId = queryParameters["reviewingListId"]
            val today = queryParameters["today"]

            if (doneListId == null || doingListId == null || testingListId == null || reviewingListId == null) {
                throw MissingParameterException("You did not provide all the 4 different list ids as query parameters")
            } else if (today == null) {
                throw MissingParameterException("You did not provide a today value as query parameters")
            } else {

                val leaderboardLists = SprintLists(
                    doneListId,
                    doingListId,
                    testingListId,
                    reviewingListId
                )

                call.respondText(
                    RequestExecuter.execute(
                        SyncTeamStatistics(
                            request.id,
                            request.GetKey(),
                            request.GetToken(),
                            today,
                            leaderboardLists
                        )
                    ),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}