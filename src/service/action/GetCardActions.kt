package nl.teqplay.trelloextension.service

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Action

class GetCardActions(private val requestInfo: RequestInfo) : BaseTrelloRequest<Array<Action>>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "cards/${requestInfo.id}/actions"
    }

    override suspend fun execute(): Array<Action> {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions
    }


}