package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.BurndownChartItem
import org.litote.kmongo.*
import kotlin.collections.toList

object BurndownChartDataSource {
    fun findAllBetweenEpochDates(
        boardId: String,
        startDate: Long,
        endDate: Long,
        database: Database.Companion.DatabaseImpl
    ): List<BurndownChartItem> {
        val collection = database.burndownChartItemCollection
        return collection.find(
            and(
                BurndownChartItem::boardId eq boardId,
                BurndownChartItem::date gte startDate,
                BurndownChartItem::date lte endDate
            )
        ).toList()
    }

    fun findWithEpochDate(date: Long, database: Database.Companion.DatabaseImpl): BurndownChartItem? {
        val collection = database.burndownChartItemCollection
        return collection.findOne(BurndownChartItem::date eq date)
    }

    fun updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(
        item: BurndownChartItem,
        database: Database.Companion.DatabaseImpl
    ) {
        val collection = database.burndownChartItemCollection
        val updateResult = collection.updateOne(BurndownChartItem::date eq item.date, item)
        if (updateResult.matchedCount == 0L) {
            collection.insertOne(item)
        }
    }
}