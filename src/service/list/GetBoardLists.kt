package nl.teqplay.trelloextension.service.list

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.service.BaseRequest

class GetBoardLists(private val boardId: String, key: String, token: String) : BaseRequest<Array<List>>() {
    private val call = TrelloCall(key, token)

    override fun prepare() {

    }

    override suspend fun execute(): Array<List> {
        call.request = "/boards/${boardId}/lists"
        return JsonHelper.fromJson(gson, call, client, Array<List>::class.java)
    }
}