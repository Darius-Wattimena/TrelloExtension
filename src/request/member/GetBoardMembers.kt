package nl.teqplay.request.member

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Member

class GetBoardMembers(private val request: Request) : BaseTrelloRequest<Array<Member>>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}/members"
    }

    override suspend fun execute(): Array<Member> {
        return JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)
    }
}