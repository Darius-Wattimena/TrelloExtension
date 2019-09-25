package nl.teqplay.trelloextension.model.sync

data class BoardSyncConfig(
    var id: String,
    var niceToHaveListId: String,
    var prioListId: String,
    var doneListId: String,
    var doingListId: String,
    var testingListId: String,
    var reviewingListId: String
)