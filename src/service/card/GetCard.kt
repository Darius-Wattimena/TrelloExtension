package nl.teqplay.trelloextension.service.card

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class GetCard(private val requestInfo: RequestInfo) : BaseTrelloRequest<Card>() {
    private val call = TrelloCall(requestInfo.key, requestInfo.token)

    override fun prepare() {
        call.request = "cards/${requestInfo.id}"
    }

    override suspend fun execute(): Card {
        return JsonHelper.fromJson(gson, call, client, Card::class.java)
    }
}