package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Board

class GetBoard(private val request: Request) : BaseTrelloRequest<Board>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}