package nl.teqplay.trelloextension.model

data class LeaderboardItem(
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
)