package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.get
import de.nielsfalk.ktor.swagger.notFound
import de.nielsfalk.ktor.swagger.ok
import de.nielsfalk.ktor.swagger.responds
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.locations.Location
import io.ktor.response.respondText
import io.ktor.routing.Routing
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.Action
import nl.teqplay.trelloextension.service.action.GetAction

@Group("Action operations")
@Location("/action/{id}")
data class action(val id: String)

fun Routing.actionRouting() {
    authenticate("basicAuth") {
        get<action>("Find an action".responds(ok<Action>(), notFound())) { card->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, card.id)
            call.respondText(
                RequestExecuter.execute(GetAction(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}