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
            if (headers["key"] == null) {
                throw MissingHeaderException("You did not provide a key when it was required")
            } else {
                key = headers["key"]!!
            }
        }
        return key
    }

    fun GetToken(): String {
        if (token.isEmpty()) {
            if (headers["token"] == null) {
                throw MissingHeaderException("You did not provide a token when it was required")
            } else {
                token = headers["token"]!!
            }
        }
        return token
    }
}