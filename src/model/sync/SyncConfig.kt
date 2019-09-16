package nl.teqplay.trelloextension.model.sync

data class SyncConfig (
    var key: String,
    var token: String,
    var boards: Array<BoardSyncConfig>
)