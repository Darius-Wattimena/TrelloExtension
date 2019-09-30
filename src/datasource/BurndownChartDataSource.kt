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

    fun updateWhenBurndownChartItemDateIsFoundOtherwiseInsert(
        item: BurndownChartItem,
        database: Database.Companion.DatabaseImpl
    ) {
        val collection = database.burndownChartItemCollection
        val updateResult = collection.updateOne(
            and(
                BurndownChartItem::date eq item.date,
                BurndownChartItem::boardId eq item.boardId
            ), item)
        if (updateResult.matchedCount == 0L) {
            collection.insertOne(item)
        }
    }
}