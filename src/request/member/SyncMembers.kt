package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.Request
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Member

class SyncMembers(private val request: Request) : BaseTrelloRequest<String>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())
    private val db = Database.instance

    override fun prepare() {
        call.request = "/boards/${request.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): String {
        val members = JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)

        for (member in members) {
            db.save(member, Member::class.java)
        }

        return "Sync Successful"
    }
}