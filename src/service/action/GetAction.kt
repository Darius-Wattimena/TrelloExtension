package nl.teqplay.trelloextension.service.action

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Action
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetAction(private val requestInfo: RequestInfo) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "actions/${requestInfo.id}"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}