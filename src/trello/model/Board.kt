package nl.teqplay.trelloextension.trello.model

data class Board(
    val id: String,
    var name: String,
    var desc: String,
    var url: String,
    var lists: Array<List>
)