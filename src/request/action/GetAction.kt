package nl.teqplay.trelloextension.request.action

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Action

class GetAction(private val requestInfo: RequestInfo) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "actions/${requestInfo.id}"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}