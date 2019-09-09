package nl.teqplay.trelloextension.trello.model

import java.util.*

data class Action(
    val id: String,
    var data: Data,
    var date: String,
    var type: String
)