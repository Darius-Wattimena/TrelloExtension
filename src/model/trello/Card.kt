package nl.teqplay.trelloextension.model.trello

data class Card(
    val id: String,
    var listId: String,
    var boardId: String,
    var name: String,
    var url: String,
    var labels: Array<Label> = emptyArray(),
    var actions: Array<Action>? = emptyArray(),
    var members: Array<Member>? = emptyArray(),
    var daysInList: Int,
    var dateAdded: String
)