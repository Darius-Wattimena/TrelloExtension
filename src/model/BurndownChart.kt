package nl.teqplay.trelloextension.model

data class BurndownChart(
    var items: MutableList<BurndownChartItem>,
    val startDate: Long,
    val endDate: Long
)