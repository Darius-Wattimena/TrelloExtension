package nl.teqplay.request.member

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.List
import nl.teqplay.trello.model.Member

class GetMember(private val request: Request) : BaseTrelloRequest<Member>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "members/${request.id}"
    }

    override suspend fun execute(): Member {
        return JsonHelper.fromJson(gson, call, client, Member::class.java)
    }
}