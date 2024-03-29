package nl.teqplay.trelloextension.service.list

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Card
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.service.BaseRequest

class GetDetailedList(private val requestInfo: RequestInfo) : BaseRequest<List>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)
    private val cardsCall = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "/lists/${requestInfo.id}"
        cardsCall.request = "/lists/${requestInfo.id}/cards"
    }

    override suspend fun execute(): List {
        val list = JsonHelper.fromJson(gson, call, client, List::class.java)
        val cards = JsonHelper.fromJson(gson, cardsCall, client, Array<Card>::class.java)
        list.cards = cards
        return list
    }
}