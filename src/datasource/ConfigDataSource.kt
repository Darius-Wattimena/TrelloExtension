package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.sync.SyncConfig
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOne

object ConfigDataSource {
    fun saveSyncConfig(config: SyncConfig, database: Database.Companion.DatabaseImpl) {
        val collection = database.syncConfigCollection
        //TODO redo logic when to update
        val updateResult = collection.updateOne(and(
            SyncConfig::key eq config.key,
            SyncConfig::token eq config.token
        ), config)

        if (updateResult.matchedCount == 0L) {
            collection.insertOne(config)
        }
    }

    fun getSyncConfig(database: Database.Companion.DatabaseImpl): SyncConfig? {
        val collection = database.syncConfigCollection
        return collection.findOne()
    }
}