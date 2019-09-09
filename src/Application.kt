package nl.teqplay.trelloextension

import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.request.GetCardActions
import nl.teqplay.trelloextension.request.burndownchart.GetBurndownChartInfo
import nl.teqplay.trelloextension.request.action.GetAction
import nl.teqplay.trelloextension.request.board.GetBoard
import nl.teqplay.trelloextension.request.board.GetBoardStatistics
import nl.teqplay.trelloextension.request.board.GetDetailedBoard
import nl.teqplay.trelloextension.request.board.GetLastBoardAction
import nl.teqplay.trelloextension.request.burndownchart.GetTodayBurndownChartInfo
import nl.teqplay.trelloextension.request.card.GetCard
import nl.teqplay.trelloextension.request.list.GetDetailedList
import nl.teqplay.trelloextension.request.list.GetList
import nl.teqplay.trelloextension.request.member.GetBoardMembers
import nl.teqplay.trelloextension.request.member.GetMember
import nl.teqplay.trelloextension.trello.Response
import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.method
import io.ktor.routing.route
import io.ktor.routing.routing
import nl.teqplay.trelloextension.request.leaderboard.GetLeaderboardData
import nl.teqplay.trelloextension.request.leaderboard.SyncBoardLeaderboardData
import nl.teqplay.trelloextension.request.member.SyncMembers

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val client = HttpClient(Apache) {
    }

    routing {
        method(HttpMethod.Get) {
            route("/") {
                route("json") {

                    /**
                     * Board
                     */

                    get("/board/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetBoard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/detailed") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetDetailedBoard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/statistics") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetBoardStatistics(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/getLastAction") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetLastBoardAction(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/processedBoard/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            getBoardsAndProcessActionsOfAllCards(request),
                            contentType = ContentType.Application.Json
                        )
                    }

                    route("/testBoard") {
                        get {
                            val request = RequestInfo(call.request.headers, "RsU5w4Bn")
                            call.respondText(
                                getBoardsAndProcessActionsOfAllCards(request),
                                contentType = ContentType.Application.Json
                            )
                        }
                    }

                    get("/board/{id}/burndownchartinfo") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        val doneListId = call.request.headers["doneListId"]
                        val startDate = call.request.headers["startDate"]
                        val endDate = call.request.headers["endDate"]
                        val today = call.request.headers["today"]
                        call.respondText(
                            RequestExecuter.execute(
                                GetBurndownChartInfo(request,
                                    doneListId.toString(),
                                    startDate.toString(),
                                    endDate.toString(),
                                    today.toString()
                                )
                            ),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/todayburndownchartinfo") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        val doneListId = call.request.headers["doneListId"]
                        val today = call.request.headers["today"]
                        call.respondText(
                            RequestExecuter.execute(
                                GetTodayBurndownChartInfo(request,
                                    doneListId.toString(),
                                    today.toString()
                                )
                            ),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/sync/leaderboard") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        val doneListId = call.request.headers["doneListId"]
                        val doingListId = call.request.headers["doingListId"]
                        val testingListId = call.request.headers["testingListId"]
                        val startDate = call.request.headers["startDate"]
                        val endDate = call.request.headers["endDate"]
                        call.respondText(
                            RequestExecuter.execute(SyncBoardLeaderboardData(
                                request,
                                doneListId.toString(),
                                doingListId.toString(),
                                testingListId.toString(),
                                startDate.toString(),
                                endDate.toString()
                            )),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/leaderboard") {
                        val startDate = call.request.headers["startDate"]
                        val endDate = call.request.headers["endDate"]
                        call.respondText(
                            RequestExecuter.execute(
                                GetLeaderboardData(call.parameters["id"]!!,
                                    startDate.toString(),
                                    endDate.toString()
                                )
                            ),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * List
                     */

                    get("/list/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetList(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/list/{id}/detailed") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetDetailedList(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Card
                     */

                    get("/card/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetCard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/card/{id}/actions") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetCardActions(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Action
                     */

                    get("/action/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetAction(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Member
                     */

                    get("member/{id}") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetMember(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("board/{id}/members") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetBoardMembers(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("board/{id}/sync/members") {
                        val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(SyncMembers(request)),
                            contentType = ContentType.Application.Json
                        )
                    }
                }
            }
        }
    }
}

suspend fun getBoardsAndProcessActionsOfAllCards(requestInfo: RequestInfo): String {
    val gson = Gson()
    return try {
        val trelloRequest = GetDetailedBoard(requestInfo)
        trelloRequest.prepare()
        val board = trelloRequest.execute()

        for (list in board.lists)
            for (card in list.cards) {
                val newRequest = requestInfo.copy(headers = requestInfo.headers, id = card.id)
                val actionRequest = GetCardActions(newRequest)
                actionRequest.prepare()
                card.actions = actionRequest.execute()
            }

        val response = Response()
        response.value = board
        gson.toJson(response)
    } catch (cause: Throwable) {
        val errorResponse = Response()
        errorResponse.error = cause.message
        gson.toJson(errorResponse)
    }
}
