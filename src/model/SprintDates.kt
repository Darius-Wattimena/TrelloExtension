package nl.teqplay.trelloextension.model

import nl.teqplay.trelloextension.helper.TimeHelper
import java.sql.Date
import java.time.LocalDate

class SprintDates(startDate: String, endDate: String) {
    var epochStartDate = TimeHelper.getISOLocalDateStringToEpochMilliseconds(startDate)
    var epochEndDate: Long = TimeHelper.getISOLocalDateStringToEpochMilliseconds(endDate)
}