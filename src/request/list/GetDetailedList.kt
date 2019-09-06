package nl.teqplay.request.list

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Card
import nl.teqplay.trello.model.List

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