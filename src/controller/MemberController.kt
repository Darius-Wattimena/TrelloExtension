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
import nl.teqplay.trelloextension.RequestExecutor
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.model.trello.Action
import nl.teqplay.trelloextension.model.trello.Member
import nl.teqplay.trelloextension.service.action.GetMemberLatestActions
import nl.teqplay.trelloextension.service.member.GetMember

@Group("Member operations")
@Location("/member/{id}")
data class member(val id: String, val key: String, val token: String)

@Group("Member operations")
@Location("/member/{id}/actions")
data class getLatestActions(val id: String, val key: String, val token: String)

fun Routing.memberRouting() {
    authenticate("basicAuth") {
        get<member>("Find a member".responds(ok<Member>(), notFound())) { member ->
            val request = RequestInfo(member.id, member.key, member.token)
            call.respondText(
                RequestExecutor.execute(GetMember(request)),
                contentType = ContentType.Application.Json
            )
        }

        get<getLatestActions>("Find a members actions".responds(ok<Model<Action>>(), notFound())) { member ->
            val request = RequestInfo(member.id, member.key, member.token)
            call.respondText(
                RequestExecutor.execute(GetMemberLatestActions(request)),
                contentType = ContentType.Application.Json
            )
        }
    }
}