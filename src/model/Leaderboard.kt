package nl.teqplay.trelloextension.model

data class Leaderboard(
    var items: MutableCollection<LeaderboardItem>,
    val startDate: Long,
    val endDate: Long
)