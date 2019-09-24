package nl.teqplay.trelloextension.service.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Board
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.model.List
import nl.teqplay.trelloextension.service.BaseRequest

class GetDetailedBoard(private val requestInfo: RequestInfo) : BaseRequest<Board>() {
    private val boardCall = TrelloCall(requestInfo.key, requestInfo.token)
    private val boardListsCall = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        boardCall.request = "/boards/${requestInfo.id}"
        boardListsCall.request = "/boards/${requestInfo.id}/lists"
    }

    override suspend fun execute(): Board {
        val board = JsonHelper.fromJson(gson, boardCall, client, Board::class.java)
        board.lists = getListAndCards(board)
        client.close()
        return board
    }

    private suspend fun getListAndCards(board: Board): Array<List> {
        val boardLists = JsonHelper.fromJson(gson, boardListsCall, client, Array<List>::class.java)

        for (list in boardLists) {
            val listCardsCall = TrelloCall(requestInfo.key, requestInfo.token)
            listCardsCall.request = "/lists/${list.id}/cards"
            val listCards = JsonHelper.fromJson(gson, listCardsCall, client, Array<Card>::class.java)
            list.cards = listCards
        }
        return boardLists
    }
}