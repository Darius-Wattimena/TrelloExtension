package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.LeaderboardItem
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.updateOne

object LeaderboardDataSource {
    fun findAllBoardItems(boardId: String, database: Database.Companion.DatabaseImpl): List<LeaderboardItem> {
        val collection = database.leaderboardItemCollection
        return collection.find(LeaderboardItem::boardId eq boardId).toList()
    }

    fun findAllBoardItems(boardId: String, startDate: Long, endDate: Long, database: Database.Companion.DatabaseImpl): List<LeaderboardItem> {
        val collection = database.leaderboardItemCollection
        return collection.find(
            and(
                LeaderboardItem::boardId eq boardId,
                LeaderboardItem::startDate eq startDate,
                LeaderboardItem::endDate eq endDate
            )
        ).toList()
    }

    fun updateWhenBoardAndMemberIdIsFoundOtherwiseInsert(item: LeaderboardItem, boardId: String, memberId: String, database: Database.Companion.DatabaseImpl) {
        val collection = database.leaderboardItemCollection
        val result = collection.updateOne(
            and(
                LeaderboardItem::boardId eq boardId,
                LeaderboardItem::memberId eq memberId
            ), item)
        if (result.matchedCount != 1L) {
            collection.insertOne(item)
        }
    }
}