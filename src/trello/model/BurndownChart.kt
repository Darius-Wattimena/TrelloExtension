package nl.teqplay.trelloextension.trello.model

import trello.model.BurndownChartItem

data class BurndownChart(
    var items: MutableMap<Long, BurndownChartItem>,
    val startDate: Long,
    val endDate: Long
)