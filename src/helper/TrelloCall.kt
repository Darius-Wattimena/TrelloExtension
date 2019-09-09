package nl.teqplay.trelloextension.helper

import nl.teqplay.trelloextension.Constants
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class TrelloCall(private val apiKey: String, private val oauthToken: String) {
    val logger = LoggerFactory.getLogger(this::class.java)
    val parameters = mutableMapOf<String, String>()
    var request = ""

    private var buildURL = ""

    /**
     * Map all the parameters from the mutable map to a string list of "key=value"
     * Then we separate them with the "&"
     */
    private fun formatParameters(): String {
        logger.debug("Formatting parameters map for a HTTP request")
        return parameters.map { (k, v) -> "$k=$v" }.joinToString("&")
    }

    /**
     * Build the URL we need to execute the HTTP request
     *
     * This URL contains all the [parameters], the [request] and some default values like the BaseURL, APIKey and OAuthToken
     */
    private fun build() {
        logger.debug("Building Trello HTTP request")
        buildURL = "${Constants.TRELLO_BASEURL}$request?${formatParameters()}&key=$apiKey&token=$oauthToken"
    }

    /**
     * Get the URL of the HTTP request from [buildURL]
     *
     * If its empty we [build] the url first
     */
    fun getURL(): String {
        if (buildURL.isEmpty()) {
            build()
        }
        return buildURL
    }

    /**
     * Execute the HTTP request with the [buildURL]
     */
    suspend inline fun execute(client: HttpClient): String {
        val url = getURL()
        logger.debug("Executing Trello HTTP request")
        val result = client.get<String>(url)
        logger.debug("Received HTTP request result")
        return result
    }
}