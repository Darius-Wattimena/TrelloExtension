package nl.teqplay.trelloextension.trello.model

import nl.teqplay.trelloextension.mongodb.Identifiable

data class DoneList(
    override var _id: String?,
    val listId: String
) : Identifiable