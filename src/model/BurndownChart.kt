package nl.teqplay.trelloextension.model

data class BurndownChart(
    var items: MutableMap<Long, BurndownChartItem>,
    val startDate: Long,
    val endDate: Long
)