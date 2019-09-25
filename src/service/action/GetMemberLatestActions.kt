package nl.teqplay.trelloextension.service.action

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Action
import nl.teqplay.trelloextension.service.BaseRequest

class GetMemberLatestActions(val requestInfo: RequestInfo) : BaseRequest<Array<Action>>() {
    var call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        val yesterday = TimeHelper.getYesterday()
        val timestamp = TimeHelper.getMongoDBTimestamp(yesterday)

        call.request = "/members/${requestInfo.id}/actions"
        call.parameters["entities"] = "false"
        call.parameters["member"] = "false"
        call.parameters["memberCreator"] = "false"
        call.parameters["since"] = timestamp
        call.parameters["limit"] = "100"
    }

    override suspend fun execute(): Array<Action> {
        return JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
    }
}