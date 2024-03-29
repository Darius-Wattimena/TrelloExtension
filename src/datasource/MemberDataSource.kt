package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.trello.Member
import org.litote.kmongo.eq
import org.litote.kmongo.updateOne

object MemberDataSource {
    fun updateWhenMemberIdIsFoundOtherwiseInsert(member: Member, database: Database.Companion.DatabaseImpl) {
        val collection = database.memberCollection
        val result = collection.updateOne(Member::id eq member.id, member)
        if (result.matchedCount != 1L) {
            collection.insertOne(member)
        }
    }

    fun findAll(database: Database.Companion.DatabaseImpl): List<Member> {
        val collection = database.memberCollection
        return collection.find().toList()
    }
}