package nl.teqplay.trelloextension.helper

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

abstract class Call {
    val logger = LoggerFactory.getLogger(this::class.java)
    val parameters = mutableMapOf<String, String>()
    var request = ""

    var buildURL = ""

    /**
     * Map all the parameters from the mutable map to a string list of "key=value"
     * Then we separate them with the "&"
     */
    fun formatParameters(): String {
        logger.debug("Formatting parameters map for a HTTP request")
        return parameters.map { (k, v) -> "$k=$v" }.joinToString("&")
    }

    /**
     * Build the URL we need to execute the HTTP request
     *
     * This URL contains all the [parameters], the [service] and some default values like the BaseURL, APIKey and OAuthToken
     */
    abstract fun build()

    /**
     * Get the URL of the HTTP request from [buildURL]
     *
     * If its empty we [build] the url first
     */
    fun getURL(): String {
        if (buildURL.isEmpty()) {
            logger.debug("Building HTTP request")
            build()
            logger.debug(buildURL)
        }
        return buildURL
    }

    /**
     * Execute the HTTP request with the [buildURL]
     */
    suspend inline fun execute(client: HttpClient): String {
        val url = getURL()
        logger.debug("Executing HTTP request")
        val result = client.get<String>(url)
        logger.debug("Received HTTP request result")
        return result
    }
}