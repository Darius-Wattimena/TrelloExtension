package nl.teqplay.trelloextension.model

class BurndownChartDetails {
    var donePoints = 0
    var doneItems = 0
    var doneHoursSpend = 0f
    var points = 0
    var items = 0
    var hoursSpend = 0f
    var missingInfo = HashMap<String, Boolean>()
}