package nl.teqplay.trelloextension.request.list

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Card
import nl.teqplay.trelloextension.trello.model.List

class GetDetailedList(private val request: Request) : BaseTrelloRequest<List>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())
    private val cardsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "lists/${request.id}"
        cardsCall.request = "lists/${request.id}/cards"
    }

    override suspend fun execute(): List {
        val list = JsonHelper.fromJson(gson, call, client, List::class.java)
        val cards = JsonHelper.fromJson(gson, cardsCall, client, Array<Card>::class.java)
        list.cards = cards
        return list
    }
}