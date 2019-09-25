package nl.teqplay.trelloextension.model.trello

data class Data(
    val member: Member,
    val card: Card,
    val listBefore: List,
    val listAfter: List
)