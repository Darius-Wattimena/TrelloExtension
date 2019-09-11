package nl.teqplay.trelloextension

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import nl.teqplay.trelloextension.controller.actionRouting
import nl.teqplay.trelloextension.controller.boardRouting
import nl.teqplay.trelloextension.controller.listRouting
import nl.teqplay.trelloextension.controller.memberRouting
import nl.teqplay.trelloextension.controller.cardRouting
import nl.teqplay.trelloextension.helper.MissingHeaderException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(StatusPages) {
        exception<MissingHeaderException> { cause ->
            cause.message?.let { call.respond(HttpStatusCode.BadRequest, it) }
        }
    }

    val client = HttpClient(Apache) {
    }

    routing {
        route("/") {
            this@routing.boardRouting()
            this@routing.listRouting()
            this@routing.cardRouting()
            this@routing.memberRouting()
            this@routing.actionRouting()
        }
    }
}