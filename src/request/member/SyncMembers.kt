package nl.teqplay.request.member

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.mongodb.DatabaseDriver
import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.model.Member

class SyncMembers(private val request: Request) : BaseTrelloRequest<String>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())
    private val driver = DatabaseDriver.instance

    override fun prepare() {
        call.request = "/boards/${request.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): String {
        val members = JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)



        return "Sync Successful"
    }
}