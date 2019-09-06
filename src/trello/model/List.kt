package nl.teqplay.trello.model

data class List(
    val id: String,
    var name: String,
    var cards: Array<Card>,
    var listLabels: Array<ListLabel>
)