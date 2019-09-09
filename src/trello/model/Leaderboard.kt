package nl.teqplay.trelloextension.trello.model

data class Leaderboard (
    var items: MutableCollection<LeaderboardItem>,
    val startDate: Long,
    val endDate: Long
)