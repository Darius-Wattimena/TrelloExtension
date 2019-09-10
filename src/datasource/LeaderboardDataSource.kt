package nl.teqplay.trelloextension.datasource

import com.mongodb.client.FindIterable
import nl.teqplay.trelloextension.model.LeaderboardItem
import org.litote.kmongo.eq

object LeaderboardDataSource {
    fun findAllBoardItems(boardId: String, database: Database.Companion.DatabaseImpl): List<LeaderboardItem> {
        val collection = database.leaderboardItemCollection
        return collection.find(LeaderboardItem::boardId eq boardId).toList()
    }
}