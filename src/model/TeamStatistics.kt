package nl.teqplay.trelloextension.model

data class TeamStatistics(
    var boardId: String,
    var date: Long,
    var totalDone: Int = 0,
    var totalDoing: Int = 0,
    var totalTesting: Int = 0,
    var totalReviewing: Int = 0
)