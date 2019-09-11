package nl.teqplay.trelloextension.helper

import io.ktor.http.Headers

data class RequestInfo(
    val headers: Headers,
    var id: String = "",
    private var key: String = "",
    private var token: String = ""
) {

    fun GetKey(): String {
        if (key.isEmpty()) {
            key = headers["key"]!!
        }
        return key
    }

    fun GetToken(): String {
        if (token.isEmpty()) {
            token = headers["token"]!!
        }
        return token
    }
}