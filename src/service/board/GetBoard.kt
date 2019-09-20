package nl.teqplay.trelloextension.service.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Board
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetBoard(private val requestInfo: RequestInfo) : BaseTrelloRequest<Board>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "boards/${requestInfo.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}