package nl.teqplay.trelloextension.service.config

import nl.teqplay.trelloextension.datasource.ConfigDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.model.sync.SyncConfig
import nl.teqplay.trelloextension.service.BaseRequest

class GetSyncConfig : BaseRequest<SyncConfig>() {
    private val db = Database.instance

    override fun prepare() {

    }

    override suspend fun execute(): SyncConfig {
        var config = ConfigDataSource.getSyncConfig(db)

        if (config == null) {
            config = SyncConfig("", "", emptyArray())
        }

        return config
    }
}