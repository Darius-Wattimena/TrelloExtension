package nl.teqplay.trelloextension.model

data class Action(
    val id: String,
    var data: Data,
    var date: String,
    var type: String
)