package nl.teqplay.trelloextension.service.board

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.ListStatistics
import nl.teqplay.trelloextension.model.Statistics
import nl.teqplay.trelloextension.model.trello.Board
import nl.teqplay.trelloextension.service.BaseRequest

class GetBoardStatistics(private val requestInfo: RequestInfo) : BaseRequest<Statistics>() {
    private val boardCall = TrelloCall(requestInfo.key, requestInfo.token)
    private val listsCall = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        boardCall.request = "/board/${requestInfo.id}"
        boardCall.parameters["fields"] = "name"

        listsCall.request = "/board/${requestInfo.id}/lists"
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