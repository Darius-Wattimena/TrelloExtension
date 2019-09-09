package nl.teqplay.trelloextension.request.list

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.List

class GetList(private val request: Request) : BaseTrelloRequest<List>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "lists/${request.id}"
    }

    override suspend fun execute(): List {
        return JsonHelper.fromJson(gson, call, client, List::class.java)
    }
}