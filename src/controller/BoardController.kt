package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.MissingParameterException
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.model.SprintLists
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

fun Routing.boardRouting() {
    authenticate("basicAuth") {
        route("board/") {
            get("{id}") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetBoard(request)),
                    contentType = ContentType.Application.Json
                )
            }

            get("{id}/detailed") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetDetailedBoard(request)),
                    contentType = ContentType.Application.Json
                )
            }

            get("{id}/statistics") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetBoardStatistics(request)),
                    contentType = ContentType.Application.Json
                )
            }

            get("{id}/getLastAction") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetLastBoardAction(request)),
                    contentType = ContentType.Application.Json
                )
            }

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

            get("{id}/teamstatistics") {
                val queryParameters = call.request.queryParameters
                val today = queryParameters["today"]
                if (today == null) {
                    throw MissingParameterException("You did not provide a today parameter")
                } else {
                    call.respondText(
                        RequestExecuter.execute(GetTeamStatistics(call.parameters["id"]!!, today)),
                        contentType = ContentType.Application.Json
                    )
                }
            }
        }
    }
}