package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.List
import nl.teqplay.trelloextension.trello.model.Member

class GetMember(private val request: Request) : BaseTrelloRequest<Member>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "members/${request.id}"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Member {
        return JsonHelper.fromJson(gson, call, client, Member::class.java)
    }
}