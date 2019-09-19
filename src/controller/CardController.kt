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
import io.ktor.routing.get
import io.ktor.routing.route
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.Action
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.service.GetCardActions
import nl.teqplay.trelloextension.service.card.GetCard
import nl.teqplay.trelloextension.service.list.GetList

@Group("Card operations")
@Location("/card/{id}")
data class card(val id: String)

@Group("Card operations")
@Location("/card/{id}/actions")
data class cardActions(val id: String)

fun Routing.cardRouting() {
    authenticate("basicAuth") {
        get<card>("Find a card".responds(ok<Card>(), notFound())) { card->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, card.id)
            call.respondText(
                RequestExecuter.execute(GetCard(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<cardActions>("Find all the card actions".responds(ok<Array<Action>>(), notFound())) { card->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, card.id)
            call.respondText(
                RequestExecuter.execute(GetCardActions(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}