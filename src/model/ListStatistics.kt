package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.model.trello.Card

data class ListStatistics(
    val name: String,
    var cards: Array<Card>?,
    var labelAmounts: MutableMap<String, Int>
)