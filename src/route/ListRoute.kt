package nl.teqplay.trelloextension.route

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.request.list.GetDetailedList
import nl.teqplay.trelloextension.request.list.GetList

fun Routing.listRouting() {
    route("list") {
        get("{id}") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetList(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/detailed") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetDetailedList(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}