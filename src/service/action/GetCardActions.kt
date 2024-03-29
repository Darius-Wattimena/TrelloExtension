package nl.teqplay.trelloextension.service.action

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Action
import nl.teqplay.trelloextension.service.BaseRequest

class GetCardActions(private val requestInfo: RequestInfo) : BaseRequest<Array<Action>>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "/cards/${requestInfo.id}/actions"
    }

    override suspend fun execute(): Array<Action> {
        return JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
    }


}