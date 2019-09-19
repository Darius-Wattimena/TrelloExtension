package nl.teqplay.trelloextension.service.config

import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.model.sync.SyncConfig
import nl.teqplay.trelloextension.service.BaseTrelloRequest

class PostSyncConfig(private val syncConfig: SyncConfig) : BaseTrelloRequest<Unit>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute() {
        ConfigDataSource.saveSyncConfig(syncConfig, db)
    }
}