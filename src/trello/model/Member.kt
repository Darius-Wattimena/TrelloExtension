package nl.teqplay.trello.model

import nl.teqplay.mongodb.Identifiable

data class Member(
    override var _id: String?,
    val id: String,
    var fullName: String,
    val role: String,
    val avatarUrl: String,
    val url: String
) : Identifiable