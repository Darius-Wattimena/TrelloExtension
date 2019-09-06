package nl.teqplay.trello.model

data class Board(
    val id: String,
    var name: String,
    var desc: String,
    var url: String,
    var lists: Array<List>
)