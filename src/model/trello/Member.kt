package nl.teqplay.trelloextension.model.trello

data class Member(
    val id: String,
    var fullName: String,
    var role: String?,
    var avatarUrl: String?,
    var url: String?
)