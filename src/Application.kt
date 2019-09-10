package nl.teqplay.trelloextension

import io.ktor.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.routing.route
import io.ktor.routing.routing
import nl.teqplay.trelloextension.controller.actionRouting
import nl.teqplay.trelloextension.controller.boardRouting
import nl.teqplay.trelloextension.controller.listRouting
import nl.teqplay.trelloextension.controller.memberRouting
import nl.teqplay.trelloextension.controller.cardRouting

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

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