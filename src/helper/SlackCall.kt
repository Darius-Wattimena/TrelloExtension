package nl.teqplay.trelloextension.helper

import nl.teqplay.trelloextension.Constants

class SlackCall(private val oauthToken: String) : Call() {
    override fun build() {

        buildURL = "${Constants.SLACK_API_BASEURL}$request?token=$oauthToken&${formatParameters()}"
    }
}