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
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import nl.teqplay.trelloextension.controller.*
import nl.teqplay.trelloextension.helper.MissingHeaderException
import java.util.*

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(StatusPages) {
        exception<MissingHeaderException> { cause ->
            cause.message?.let { call.respond(HttpStatusCode.BadRequest, it) }
        }
    }

    install(CustomTimerFeature) {
        timer = Timer()
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).also {
            it.set(Calendar.HOUR_OF_DAY, 2)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
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