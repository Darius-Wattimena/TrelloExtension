package nl.teqplay.trelloextension.request.member

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.mongodb.Database
import nl.teqplay.trelloextension.request.BaseTrelloRequest
import nl.teqplay.trelloextension.trello.model.Member
import org.litote.kmongo.eq

class SyncMembers(private val requestInfo: RequestInfo) : BaseTrelloRequest<String>() {
    private val call = TrelloCall(requestInfo.GetKey(), requestInfo.GetToken())
    private val db = Database.instance

    override fun prepare() {
        call.request = "/boards/${requestInfo.id}/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): String {
        val members = JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)

        for (member in members) {
            db.saveWhen(member, Member::class.java, Member::id eq member.id)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}