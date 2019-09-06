package nl.teqplay.request.action

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Action

class GetAction(private val request: Request) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "actions/${request.id}"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}