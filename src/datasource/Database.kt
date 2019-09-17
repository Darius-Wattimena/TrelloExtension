package nl.teqplay.trelloextension.datasource

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.typesafe.config.ConfigFactory
import nl.teqplay.trelloextension.model.BurndownChartItem
import nl.teqplay.trelloextension.model.LeaderboardItem
import nl.teqplay.trelloextension.model.Member
import nl.teqplay.trelloextension.model.TeamStatistics
import nl.teqplay.trelloextension.model.sync.SyncConfig
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class Database {
    companion object {
        val instance = DatabaseImpl()

        class DatabaseImpl internal constructor() {
            private val config = ConfigFactory.load()
            private val dbConfig = config.getConfig("ktor").getConfig("application").getConfig("db")
            private val dbName = dbConfig.getString("db_name")
            private val host = dbConfig.getString("db_host")
            private val port = dbConfig.getInt("db_port")
            private val username = dbConfig.getString("username")
            private val password = dbConfig.getString("password")

            private val credentials = MongoCredential.createCredential(
                username, dbName, password.toCharArray()
            )

            private val client = KMongo.createClient(
                ServerAddress(host, port),
                listOf(credentials)
            )
            private val database = client.getDatabase(dbName)

            val burndownChartItemCollection = database.getCollection<BurndownChartItem>()
            val memberCollection = database.getCollection<Member>()
            val leaderboardItemCollection = database.getCollection<LeaderboardItem>()
            val teamStatisticsCollection = database.getCollection<TeamStatistics>()
            val syncConfigCollection = database.getCollection<SyncConfig>()
        }
    }
}