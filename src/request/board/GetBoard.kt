package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Board

class GetBoard(private val requestInfo: RequestInfo) : BaseTrelloRequest<Board>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "boards/${requestInfo.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}