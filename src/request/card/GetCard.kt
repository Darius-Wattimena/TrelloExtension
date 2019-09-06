package nl.teqplay.request.card

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Card

class GetCard(private val request: Request) : BaseTrelloRequest<Card>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "cards/${request.id}"
    }

    override suspend fun execute(): Card {
        return JsonHelper.fromJson(gson, call, client, Card::class.java)
    }
}