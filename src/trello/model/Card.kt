package nl.teqplay.trello.model

data class Card(
    val id: String,
    var name: String,
    var labels: Array<Label>,
    var actions: Array<Action>
)