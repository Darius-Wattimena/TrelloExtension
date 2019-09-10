package nl.teqplay.trelloextension.model

data class List(
    var id: String,
    var name: String,
    var cards: Array<Card>,
    var listLabels: Array<ListLabel>
)