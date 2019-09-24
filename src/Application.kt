package nl.teqplay.trelloextension

import de.nielsfalk.ktor.swagger.SwaggerSupport
import de.nielsfalk.ktor.swagger.version.shared.Information
import de.nielsfalk.ktor.swagger.version.v2.Swagger
import de.nielsfalk.ktor.swagger.version.v3.OpenApi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.ClientRequestException
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import nl.teqplay.trelloextension.controller.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.Executors

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

val numberSchemaMap = mapOf(
    "type" to "number",
    "value" to 0
)

val booleanSchemaMap = mapOf(
    "type" to "boolean",
    "value" to listOf(true, false)
)

val stringSchemaMap = mapOf(
    "type" to "string",
    "value" to "something"
)

val burndownchartItemSchemaMap = mapOf(
    "type" to "object",
    "properties" to mapOf(
        "date" to numberSchemaMap,
        "totalDonePoint" to numberSchemaMap,
        "totalDoneItems" to numberSchemaMap,
        "totalDoneHoursSpend" to numberSchemaMap,
        "totalPoint" to numberSchemaMap,
        "totalItems" to numberSchemaMap,
        "totalHoursSpend" to numberSchemaMap,
        "missingInfoCards" to mapOf(
            "type" to "object",
            "properties" to mapOf(
                "cardId" to stringSchemaMap,
                "missing" to booleanSchemaMap
            )
        )
    )
)

val burndownchartSchemaMap = mapOf(
    "type" to "object",
    "properties" to mapOf(
        "items" to burndownchartItemSchemaMap,
        "startDate" to stringSchemaMap,
        "endDate" to stringSchemaMap
    )
)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(@Suppress("UNUSED_PARAMETER") testing: Boolean = false) {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(StatusPages) {
        exception<ClientRequestException> { cause ->
            cause.message?.let { call.respond(HttpStatusCode.BadRequest, it) }
        }
    }

    install(Authentication) {
        basic("basicAuth") {
            realm = "backend-server"
            validate { credentials ->
                if (credentials.name == "test") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    install(CustomTimerFeature) {
        scheduler = Executors.newScheduledThreadPool(1)
        zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
    }

    install(CORS) {
        allowNonSimpleContentTypes = true

        host("localhost:3000", listOf("http", "https"))
        host("localhost:8080", listOf("http", "https"))
        host("trelloextension.eu-west-1.elasticbeanstalk.com", listOf("http", "https"))
        //TODO add frontend host

        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Options)

        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlRequestHeaders)
        header(HttpHeaders.AccessControlRequestMethod)
    }

    install(Locations)
    install(SwaggerSupport) {
        forwardRoot = true
        val information = Information(
            version = "0.1",
            title = "TrackAndTrello Backend API",
            description = "Test description"
        )
        swagger = Swagger().apply {
            info = information
            definitions["Burndownchart"] = burndownchartSchemaMap
        }
        openApi = OpenApi().apply {
            info = information
            components.schemas["Burndownchart"] = burndownchartSchemaMap
        }
    }

    routing {
        route("/") {
            this@routing.boardRouting()
            this@routing.listRouting()
            this@routing.cardRouting()
            this@routing.memberRouting()
            this@routing.actionRouting()
            this@routing.configRouting()
        }

    }
}