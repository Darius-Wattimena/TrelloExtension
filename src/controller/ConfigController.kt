package nl.teqplay.trelloextension.controller

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.service.config.PostSyncConfig

fun Routing.configRouting() {
    route("/config") {
        post("/sync") {
            RequestExecuter.execute(PostSyncConfig(call.receiveText()))
            call.respond(HttpStatusCode.Accepted, "Completed save")
        }
    }
}