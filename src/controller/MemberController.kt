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
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.service.member.GetMember

@Group("Member operations")
@Location("member/{id}")
data class member(val id: String, val key: String, val token: String)

fun Routing.memberRouting() {
    authenticate("basicAuth") {
        get<member>("Find a member".responds(ok<Member>(), notFound())) { member ->
            val queryParameters = call.request.queryParameters
            val request = RequestInfo(member.id, member.key, member.token)
            call.respondText(
                RequestExecuter.execute(GetMember(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}