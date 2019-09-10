package nl.teqplay.trelloextension.datasource

import com.mongodb.client.MongoCollection
import nl.teqplay.trelloextension.model.BurndownChartItem
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.updateOne

object BurndownChartDataSource {
    fun findWithEpochDate(date: Long, database: Database.Companion.DatabaseImpl) : BurndownChartItem? {
        val collection = database.burndownChartItemCollection
        return collection.findOne(BurndownChartItem::date eq date)
    }

    fun updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(item: BurndownChartItem, database: Database.Companion.DatabaseImpl) {
        val collection = database.burndownChartItemCollection
        collection.updateOne(BurndownChartItem::date eq item.date, item)
    }
}