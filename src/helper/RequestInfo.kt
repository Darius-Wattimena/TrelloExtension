package nl.teqplay.trelloextension.helper

import io.ktor.http.Parameters

data class RequestInfo(
    val parameters: Parameters,
    var id: String = ""
) {
    private var key: String = ""
    private var token: String = ""

    fun GetKey(): String {
        if (key.isEmpty()) {
            if (parameters["key"] == null) {
                throw MissingParameterException("You did not provide a key when it was required")
            } else {
                key = parameters["key"]!!
            }
        }
        return key
    }

    fun GetToken(): String {
        if (token.isEmpty()) {
            if (parameters["token"] == null) {
                throw MissingParameterException("You did not provide a token when it was required")
            } else {
                token = parameters["token"]!!
            }
        }
        return token
    }
}