package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.helper.TimeHelper

class SprintDates(startDate: String, endDate: String) {
    var epochStartDate = TimeHelper.getEpochMillisecondsFromISOLocalDate(startDate)
    var epochEndDate: Long = TimeHelper.getEpochMillisecondsFromISOLocalDate(endDate)
}