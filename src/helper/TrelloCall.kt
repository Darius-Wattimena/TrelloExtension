package nl.teqplay.trelloextension.helper

import nl.teqplay.trelloextension.Constants

class TrelloCall(private val apiKey: String, private val oauthToken: String) : Call() {
    override fun build() {
        buildURL = "${Constants.TRELLO_API_BASEURL}$request?key=$apiKey&token=$oauthToken&${formatParameters()}"
    }
}