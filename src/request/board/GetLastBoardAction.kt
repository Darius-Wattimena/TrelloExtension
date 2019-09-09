package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Action

class GetLastBoardAction(val request: Request) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}/actions"
        call.parameters["limit"] = "1"
        call.parameters["actions_fields"] = "type,date"
    }

    override suspend fun execute(): Action {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions.first()
    }
}