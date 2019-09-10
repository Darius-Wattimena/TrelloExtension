package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.datasource.Identifiable

data class DoneList(
    override var _id: String?,
    val listId: String
) : Identifiable