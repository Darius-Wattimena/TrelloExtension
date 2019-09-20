package nl.teqplay.trelloextension.service.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetMember(private val requestInfo: RequestInfo) : BaseTrelloRequest<Member>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "members/${requestInfo.id}"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Member {
        return JsonHelper.fromJson(gson, call, client, Member::class.java)
    }
}