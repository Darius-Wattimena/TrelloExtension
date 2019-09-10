package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.model.BurndownChartItem
import nl.teqplay.trelloextension.model.LeaderboardItem
import nl.teqplay.trelloextension.model.Member
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class Database {
    companion object {
        val instance = DatabaseImpl()

        class DatabaseImpl internal constructor() {
            private val client = KMongo.createClient()
            private val database = client.getDatabase(Constants.DATABASE_NAME)

            val burndownChartItemCollection = database.getCollection<BurndownChartItem>()
            val memberCollection = database.getCollection<Member>()
            val leaderboardItemCollection = database.getCollection<LeaderboardItem>()
        }
    }
}