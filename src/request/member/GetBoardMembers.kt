package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Member

class GetBoardMembers(private val request: Request) : BaseTrelloRequest<Array<Member>>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): Array<Member> {
        return JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)
    }
}