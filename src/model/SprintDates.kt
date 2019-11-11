package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.helper.TimeHelper

class SprintDates(var startDate: String, var endDate: String) {
    var epochStartDate = TimeHelper.getEpochMillisecondsFromISOLocalDate(startDate)
    var epochEndDate: Long = TimeHelper.getEpochMillisecondsFromISOLocalDate(endDate)
}