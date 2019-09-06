package nl.teqplay.trello.model

import trello.model.BurndownChartItem

data class BurndownChart(
    var items: MutableMap<Long, BurndownChartItem>,
    val startDate: Long,
    val endDate: Long
)