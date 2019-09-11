package nl.teqplay.trelloextension.model

data class LeaderboardItem(
    val boardId: String,
    val memberId: String,
    val name: String,
    var avatarUrl: String?,
    var assignedTasks: Int = 0,
    var doingTasks: Int = 0,
    var doneTasks: Int = 0,
    var testingTasks: Int = 0,
    var reviewingTasks: Int = 0,
    var startDate: Long = 0L,
    var endDate: Long = 0L
)