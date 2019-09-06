package trello.model

data class BurndownChartItem(
    val date: Long,
    val totalDonePoint: Int,
    val totalDoneItems: Int,
    val totalDoneHoursSpend: Float,
    val totalPoint: Int,
    val totalItems: Int,
    val totalHoursSpend: Float,
    val missingInfoCards: HashMap<String, Boolean>
)
