package nl.teqplay.mongodb

import nl.teqplay.trello.model.Board
import nl.teqplay.trello.model.DoneList
import org.litote.kmongo.*
import trello.model.BurndownChartItem

class DatabaseDriver {
    val client = KMongo.createClient()
    private val database = client.getDatabase("test")
    private val burndownChartItemCollection = database.getCollection<BurndownChartItem>()
    private val doneListCollection = database.getCollection<DoneList>()

    fun saveBurndownChartItem(item: BurndownChartItem) {
        val dbItem = burndownChartItemCollection.findOne(BurndownChartItem::date eq item.date)
        if (dbItem == null) {
            burndownChartItemCollection.insertOne(item)
        } else {
            burndownChartItemCollection.updateOne(BurndownChartItem::date eq dbItem.date, item)
        }
    }

    fun findBurndownChartItem(epochDate: Long) : BurndownChartItem? {
        return burndownChartItemCollection.findOne(BurndownChartItem::date eq epochDate)
    }

    fun saveDoneList(board: Board, listId: String) {
        val dbItem = doneListCollection.findOne(Board::id eq board.id)
        val item = DoneList(board.id, listId)
        if (dbItem == null) {
            doneListCollection.insertOne(item)
        } else {
            doneListCollection.updateOne(Board::id eq dbItem.boardId, item)
        }
    }

    fun findDoneList(boardId: String): DoneList? {
        return doneListCollection.findOne(DoneList::boardId eq boardId)
    }
}