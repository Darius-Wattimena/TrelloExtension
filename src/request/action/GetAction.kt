package nl.teqplay.trelloextension.request.action

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Action

class GetAction(private val request: Request) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "actions/${request.id}"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}