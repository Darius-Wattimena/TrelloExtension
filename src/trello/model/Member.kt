package nl.teqplay.trelloextension.trello.model

import nl.teqplay.trelloextension.mongodb.Identifiable

data class Member(
    override var _id: String?,
    val id: String,
    var fullName: String,
    val role: String,
    val avatarUrl: String,
    val url: String
) : Identifiable