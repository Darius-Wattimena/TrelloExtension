package nl.teqplay.trelloextension.route

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.request.GetCardActions
import nl.teqplay.trelloextension.request.card.GetCard

fun Routing.cardRouting() {
    route("card/") {
        get("{id}") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetCard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get("{id}/actions") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetCardActions(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}