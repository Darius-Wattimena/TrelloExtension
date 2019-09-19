package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.locations.Location
import io.ktor.response.respondText
import io.ktor.routing.Routing
import nl.teqplay.trelloextension.RequestExecuter
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.service.list.GetDetailedList
import nl.teqplay.trelloextension.service.list.GetList

@Group("List operations")
@Location("list/{id}")
data class list(val id: String)

@Group("List operations")
@Location("list/{id}/detailed")
data class detailed(val id: String)

fun Routing.listRouting() {
    authenticate("basicAuth") {
        get<list>("Find a list".responds(ok<List>(), notFound())) { list->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, list.id)
            call.respondText(
                RequestExecuter.execute(GetList(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<detailed>("Find detailed list".responds(ok<List>(), notFound())) { listDetailed ->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(queryParameters, listDetailed.id)
            call.respondText(
                RequestExecuter.execute(GetDetailedList(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}