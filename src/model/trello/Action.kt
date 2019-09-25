package nl.teqplay.trelloextension.model.trello

data class Action(
    val id: String,
    var data: Data,
    var date: String,
    var type: String
)