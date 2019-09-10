package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.datasource.Identifiable

data class Member(
    override var _id: String?,
    val id: String,
    var fullName: String,
    var role: String?,
    var avatarUrl: String?,
    var url: String?
) : Identifiable