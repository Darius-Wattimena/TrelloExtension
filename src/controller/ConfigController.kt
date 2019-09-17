package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.service.config.GetSyncConfig
import nl.teqplay.trelloextension.service.config.PostSyncConfig

fun Routing.configRouting() {
    route("/config") {
        get("/getSyncInfo") {
            call.respondText(
                RequestExecuter.execute(GetSyncConfig()),
                contentType = ContentType.Application.Json
            )
        }

        put("/saveSync") {
            RequestExecuter.execute(PostSyncConfig(call.receiveText()))
            call.respond(HttpStatusCode.Accepted, "Completed save")
        }
    }
}