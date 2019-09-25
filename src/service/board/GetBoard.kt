package nl.teqplay.trelloextension.service.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Board
import nl.teqplay.trelloextension.service.BaseRequest

class GetBoard(private val requestInfo: RequestInfo) : BaseRequest<Board>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "/boards/${requestInfo.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}