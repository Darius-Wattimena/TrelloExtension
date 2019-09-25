package nl.teqplay.trelloextension.service.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Action
import nl.teqplay.trelloextension.service.BaseRequest

class GetLastBoardAction(val requestInfo: RequestInfo) : BaseRequest<Action>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "/boards/${requestInfo.id}/actions"
        call.parameters["limit"] = "1"
        call.parameters["actions_fields"] = "type,date"
    }

    override suspend fun execute(): Action {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        return actions.first()
    }
}