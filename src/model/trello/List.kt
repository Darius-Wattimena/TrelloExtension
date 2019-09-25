package nl.teqplay.trelloextension.model.trello

data class List(
    var id: String,
    var name: String,
    var cards: Array<Card>,
    var listLabels: Array<ListLabel>
)