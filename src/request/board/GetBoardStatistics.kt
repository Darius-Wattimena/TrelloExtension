package nl.teqplay.trelloextension.request.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Board
import nl.teqplay.trelloextension.trello.model.ListStatistics
import nl.teqplay.trelloextension.trello.model.Statistics

class GetBoardStatistics(private val request: Request) : BaseTrelloRequest<Statistics>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val listsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        boardCall.request = "board/${request.id}"
        boardCall.parameters["fields"] = "name"

        listsCall.request = "board/${request.id}/lists"
        listsCall.parameters["fields"] = "name"
        listsCall.parameters["cards"] = "all"
        listsCall.parameters["card_fields"] = "labels, name"
    }

    override suspend fun execute(): Statistics {
        val board = JsonHelper.fromJson(gson, boardCall, client, Board::class.java)

        val lists = JsonHelper.fromJson(gson, listsCall, client, Array<ListStatistics>::class.java)

        for (list in lists) {
            list.labelAmounts = HashMap()
            for (card in list.cards!!) {
                for (label in card.labels) {
                    if (!list.labelAmounts.containsKey(label.name)) {
                        list.labelAmounts[label.name] = 1
                    } else {
                        val newValue = list.labelAmounts[label.name]!! + 1
                        list.labelAmounts[label.name] = newValue
                    }
                }
            }

            list.cards = null
        }

        return Statistics(board.id, board.name, lists)
    }
}