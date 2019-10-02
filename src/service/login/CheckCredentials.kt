package nl.teqplay.trelloextension.service.login

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import nl.teqplay.trelloextension.service.BaseRequest

class CheckCredentials(val username: String, val password: String) : BaseRequest<Boolean>() {
    private var configUsername: String = ""
    private var configPassword: String = ""

    override fun prepare() {
        val config = HoconApplicationConfig(ConfigFactory.load())
        val authConfig = config.config("ktor.application.basic_auth")
        configUsername = authConfig.property("username").getString()
        configPassword = authConfig.property("password").getString()
    }

    override suspend fun execute(): Boolean {
        return (username == configUsername && password == configPassword)
    }
}