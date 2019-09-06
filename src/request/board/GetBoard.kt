package nl.teqplay.request.board

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Board

class GetBoard(private val request: Request) : BaseTrelloRequest<Board>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}