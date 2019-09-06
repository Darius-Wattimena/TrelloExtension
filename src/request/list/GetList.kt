package nl.teqplay.request.list

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.List

class GetList(private val request: Request) : BaseTrelloRequest<List>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "lists/${request.id}"
    }

    override suspend fun execute(): List {
        return JsonHelper.fromJson(gson, call, client, List::class.java)
    }
}