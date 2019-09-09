package nl.teqplay.trelloextension.trello.model

import nl.teqplay.trelloextension.mongodb.Identifiable

data class LeaderboardItem(
    override var _id: String?,
    val boardId: String,
    val memberId: String,
    val name: String,
    var avatarUrl: String?,
    var assignedTasks: Int,
    var doingTasks: Int,
    var doneTasks: Int,
    var testingTasks: Int,
    var startDate: Long,
    var endDate: Long
) : Identifiable