package nl.teqplay.trelloextension.model

data class Statistics(
    val boardId: String,
    val boardName: String,
    val listsStatistics: Array<ListStatistics>
)