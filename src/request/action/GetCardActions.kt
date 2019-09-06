package nl.teqplay.request

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.trello.model.Action

class GetCardActions(private val request: Request) : BaseTrelloRequest<Array<Action>>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "cards/${request.id}/actions"
    }

    override suspend fun execute(): Array<Action> {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions
    }


}