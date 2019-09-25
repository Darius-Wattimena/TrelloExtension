package nl.teqplay.trelloextension.controller

import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Group
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import nl.teqplay.trelloextension.RequestExecutor
import nl.teqplay.trelloextension.model.trello.Member
import nl.teqplay.trelloextension.model.sync.SyncConfig
import nl.teqplay.trelloextension.service.config.GetSyncConfig
import nl.teqplay.trelloextension.service.config.PostSyncConfig

@Group("Config operations")
@Location("/config/getSyncInfo")
class syncInfo

@Group("Config operations")
@Location("/config/saveSyncInfo")
class saveSyncInfo

fun Routing.configRouting() {
    authenticate("basicAuth") {
        get<syncInfo>("Find sync info".responds(ok<Member>(), notFound())) {
            call.respondText(
                RequestExecutor.execute(GetSyncConfig()),
                contentType = ContentType.Application.Json
            )
        }

        put<saveSyncInfo, SyncConfig>("Save sync info".responds(ok<String>(), notFound())) { syncCall, syncConfig ->
            RequestExecutor.execute(PostSyncConfig(syncConfig))
            call.respond(HttpStatusCode.Accepted, "Completed save")
        }
    }
}