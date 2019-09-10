package nl.teqplay.trelloextension.model

data class Card(
    val id: String,
    var name: String,
    var labels: Array<Label>,
    var actions: Array<Action>,
    var members: Array<Member>
)