package nl.teqplay.trelloextension.model

data class BoardLists(
    val NiceToHaveListId: String,
    val PrioListId: String,
    val DoingListId: String,
    val ReviewingListId: String,
    val TestingListId: String,
    val ReadyListId: String,
    val ImpedimentsListId: String,
    val DoneListId: String
)