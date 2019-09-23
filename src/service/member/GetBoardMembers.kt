package nl.teqplay.trelloextension.service.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.service.BaseRequest

class GetBoardMembers(private val requestInfo: RequestInfo) : BaseRequest<Array<Member>>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "boards/${requestInfo.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Array<Member> {
        return JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)
    }
}