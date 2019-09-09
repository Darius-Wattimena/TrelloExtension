package trello.model

import nl.teqplay.mongodb.Identifiable

data class BurndownChartItem(
    override var _id: String?,
    val date: Long,
    val totalDonePoint: Int,
    val totalDoneItems: Int,
    val totalDoneHoursSpend: Float,
    val totalPoint: Int,
    val totalItems: Int,
    val totalHoursSpend: Float,
    val missingInfoCards: HashMap<String, Boolean>
) : Identifiable
