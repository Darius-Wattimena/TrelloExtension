package nl.teqplay.trelloextension.request.card

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Card

class GetCard(private val request: Request) : BaseTrelloRequest<Card>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "cards/${request.id}"
    }

    override suspend fun execute(): Card {
        return JsonHelper.fromJson(gson, call, client, Card::class.java)
    }
}