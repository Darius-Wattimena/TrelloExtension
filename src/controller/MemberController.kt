package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.service.member.GetMember

fun Routing.memberRouting() {
    route("member/") {
        get("{id}") {
            val request = RequestInfo(call.request.headers, call.parameters["id"]!!)
            call.respondText(
                RequestExecuter.execute(GetMember(request)),
                contentType = ContentType.Application.Json
            )
        }
    }

}