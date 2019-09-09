package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Member

class GetMember(private val requestInfo: RequestInfo) : BaseTrelloRequest<Member>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "members/${requestInfo.id}"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Member {
        return JsonHelper.fromJson(gson, call, client, Member::class.java)
    }
}