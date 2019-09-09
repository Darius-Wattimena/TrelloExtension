package nl.teqplay.trelloextension.request.list

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Card
import nl.teqplay.trelloextension.trello.model.List

class GetDetailedList(private val requestInfo: RequestInfo) : BaseTrelloRequest<List>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
    private val cardsCall = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())

    override fun prepare() {
        call.request = "lists/${requestInfo.id}"
        cardsCall.request = "lists/${requestInfo.id}/cards"
    }

    override suspend fun execute(): List {
        val list = JsonHelper.fromJson(gson, call, client, List::class.java)
        val cards = JsonHelper.fromJson(gson, cardsCall, client, Array<Card>::class.java)
        list.cards = cards
        return list
    }
}