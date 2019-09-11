package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.SprintDates
import nl.teqplay.trelloextension.service.board.GetBoard
import nl.teqplay.trelloextension.service.board.GetBoardStatistics
import nl.teqplay.trelloextension.service.board.GetDetailedBoard
import nl.teqplay.trelloextension.service.board.GetLastBoardAction
import nl.teqplay.trelloextension.service.burndownchart.GetBurndownChartInfo
import nl.teqplay.trelloextension.service.burndownchart.SyncBurndownChartInfo
import nl.teqplay.trelloextension.service.leaderboard.GetLeaderboardData
import nl.teqplay.trelloextension.service.leaderboard.SyncBoardLeaderboardData
import nl.teqplay.trelloextension.service.member.GetBoardMembers
import nl.teqplay.trelloextension.service.member.SyncMembers

fun Routing.boardRouting() {
    route("board/") {
        get("{id}") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/detailed") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetDetailedBoard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/statistics") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetBoardStatistics(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/getLastAction") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetLastBoardAction(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/burndownchartinfo") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            val startDate = call.request.headers["startDate"]
            val endDate = call.request.headers["endDate"]
            val sprintDates = SprintDates(startDate, endDate)

            call.respondText(
                RequestExecuter.execute(
                    GetBurndownChartInfo(
                        request,
                        sprintDates
                    )
                ),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/sync/burndownchartinfo") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            val doneListId = call.request.headers["doneListId"]
            val today = call.request.headers["today"]
            call.respondText(
                RequestExecuter.execute(
                    SyncBurndownChartInfo(
                        request,
                        doneListId.toString(),
                        today.toString()
                    )
                ),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/sync/leaderboard") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            val doneListId = call.request.headers["doneListId"]
            val doingListId = call.request.headers["doingListId"]
            val testingListId = call.request.headers["testingListId"]
            val reviewingListId = call.request.headers["reviewingListId"]
            val startDate = call.request.headers["startDate"]
            val endDate = call.request.headers["endDate"]
            call.respondText(
                RequestExecuter.execute(
                    SyncBoardLeaderboardData(
                        request,
                        doneListId.toString(),
                        doingListId.toString(),
                        testingListId.toString(),
                        reviewingListId.toString(),
                        startDate.toString(),
                        endDate.toString()
                    )
                ),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/leaderboard") {
            val startDate = call.request.headers["startDate"]
            val endDate = call.request.headers["endDate"]
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
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetBoardMembers(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/sync/members") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(SyncMembers(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}