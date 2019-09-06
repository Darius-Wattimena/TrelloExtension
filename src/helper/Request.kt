package nl.teqplay.helper

import io.ktor.http.Headers

data class Request(
    val headers: Headers,
    var id: String = "",
    private var key: String = "",
    private var token: String = ""
) {

    fun GetKey(): String {
        if (key.isEmpty()) {
            key = headers["key"].toString()
        }
        return key
    }

    fun GetToken(): String {
        if (token.isEmpty()) {
            token = headers["token"].toString()
        }
        return token
    }
}