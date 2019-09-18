package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.service.GetCardActions
import nl.teqplay.trelloextension.service.card.GetCard

fun Routing.cardRouting() {
    authenticate("basicAuth") {
        route("card/") {
            get("{id}") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetCard(request)),
                    contentType = ContentType.Application.Json
                )
            }

            get("{id}/actions") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetCardActions(request)),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}