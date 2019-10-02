package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.HttpCodeResponse
import de.nielsfalk.ktor.swagger.get
import de.nielsfalk.ktor.swagger.ok
import de.nielsfalk.ktor.swagger.responds
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.response.respond
import io.ktor.routing.Routing
import nl.teqplay.trelloextension.service.login.CheckCredentials


@Group("Login operations")
@Location("/login")
data class login(val username: String, val password: String)

fun Routing.loginRouting() {
    get<login>("Find a list".responds(ok<Any>(), HttpCodeResponse(HttpStatusCode.Unauthorized, listOf()))) { login ->
        val service = CheckCredentials(login.username, login.password)
        service.prepare()
        val correctCredentials = service.execute()
        if (correctCredentials) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}