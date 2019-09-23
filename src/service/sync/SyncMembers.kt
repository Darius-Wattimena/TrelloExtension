package nl.teqplay.trelloextension.service.sync

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.datasource.MemberDataSource
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.service.BaseRequest

class SyncMembers(private val boardId: String, apiKey: String, token: String) : BaseRequest<String>() {
    private val call = TrelloCall(apiKey, token)
    private val db = Database.instance

    override fun prepare() {
        call.request = "/boards/$boardId/members"
        call.parameters["fields"] = "fullName,name,role,avatarUrl,url"
    }

    override suspend fun execute(): String {
        val members = JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)

        for (member in members) {
            MemberDataSource.updateWhenMemberIdIsFoundOtherwiseInsert(member, db)
        }

        return Constants.SYNC_SUCCESS_RESPONSE
    }
}