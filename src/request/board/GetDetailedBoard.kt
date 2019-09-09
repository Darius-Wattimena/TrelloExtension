package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Board
import nl.teqplay.trelloextension.trello.model.Card
import nl.teqplay.trelloextension.trello.model.List

class GetDetailedBoard(private val request: Request) : BaseTrelloRequest<Board>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val boardListsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        boardCall.request = "boards/${request.id}"
        boardListsCall.request = "boards/${request.id}/lists"
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
            val listCardsCall = TrelloCall(request.GetKey(), request.GetToken())
            listCardsCall.request = "lists/${list.id}/cards"
            val listCards = JsonHelper.fromJson(gson, listCardsCall, client, Array<Card>::class.java)
            list.cards = listCards
        }
        return boardLists
    }
}