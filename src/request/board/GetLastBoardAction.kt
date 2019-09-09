package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Action

class GetLastBoardAction(val requestInfo: RequestInfo) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "boards/${requestInfo.id}/actions"
        call.parameters["limit"] = "1"
        call.parameters["actions_fields"] = "type,date"
    }

    override suspend fun execute(): Action {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions.first()
    }
}