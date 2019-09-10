package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.datasource.Identifiable

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
    var reviewingTasks: Int,
    var startDate: Long,
    var endDate: Long
) : Identifiable