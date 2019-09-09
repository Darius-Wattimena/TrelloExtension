package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Member

class GetBoardMembers(private val requestInfo: RequestInfo) : BaseTrelloRequest<Array<Member>>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "boards/${requestInfo.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Array<Member> {
        return JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)
    }
}