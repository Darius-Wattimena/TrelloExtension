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
import nl.teqplay.trelloextension.service.member.GetMember

fun Routing.memberRouting() {
    authenticate("basicAuth") {
        route("member/") {
            get("{id}") {
                val queryParameters = call.request.queryParameters
                val request = RequestInfo(queryParameters, call.parameters["id"]!!)
                call.respondText(
                    RequestExecuter.execute(GetMember(request)),
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}