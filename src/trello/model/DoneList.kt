package nl.teqplay.trello.model

import nl.teqplay.mongodb.Identifiable

data class DoneList(
    override var _id: String?,
    val listId: String
) : Identifiable