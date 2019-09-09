package nl.teqplay.trelloextension.trello.model

data class Statistics(
    val boardId: String,
    val boardName: String,
    val listsStatistics: Array<ListStatistics>
)