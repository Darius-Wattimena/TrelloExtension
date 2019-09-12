package nl.teqplay.trelloextension.model

data class SprintLists(
    var doneListId: String,
    var doingListId: String,
    var testingListId: String,
    var reviewingListId: String
)