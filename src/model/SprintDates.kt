package nl.teqplay.trelloextension.model

import java.sql.Date
import java.time.LocalDate

class SprintDates(startDate: String?, endDate: String?) {
    var startLocalDate: LocalDate = LocalDate.parse(startDate)
    var endLocalDate: LocalDate = LocalDate.parse(endDate)
    var epochStartDate: Long = Date.valueOf(startDate).time
    var epochEndDate: Long = Date.valueOf(endDate).time
}