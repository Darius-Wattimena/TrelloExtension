package nl.teqplay.trelloextension.datasource

import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import nl.teqplay.trelloextension.model.*
import nl.teqplay.trelloextension.model.sync.SyncConfig
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class Database {
    companion object {
        val instance = DatabaseImpl()

        class DatabaseImpl internal constructor() {
            private val config = HoconApplicationConfig(ConfigFactory.load())
            private val databaseConfig = config.config("ktor.application.db")
            private val name = databaseConfig.property("name").getString()
            private val host = databaseConfig.property("host").getString()
            private val port = databaseConfig.property("port").getString().toInt()
            private val username = databaseConfig.property("username").getString()
            private val password = databaseConfig.property("password").getString()

            private val credentials = MongoCredential.createCredential(
                username, name, password.toCharArray()
            )

            private val client = KMongo.createClient(
                ServerAddress(host, port),
                listOf(credentials)
            )
            private val database = client.getDatabase(name)

            val burndownChartItemCollection = database.getCollection<BurndownChartItem>()
            val memberCollection = database.getCollection<Member>()
            val leaderboardItemCollection = database.getCollection<LeaderboardItem>()
            val teamStatisticsCollection = database.getCollection<TeamStatistics>()
            val syncConfigCollection = database.getCollection<SyncConfig>()
            val cardCollection = database.getCollection<Card>()
        }
    }
}