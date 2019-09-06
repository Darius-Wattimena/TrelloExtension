package nl.teqplay.mongodb

import com.mongodb.client.MongoCollection
import nl.teqplay.Constants
import nl.teqplay.trello.model.Board
import nl.teqplay.trello.model.DoneList
import nl.teqplay.trello.model.Member
import org.litote.kmongo.*
import trello.model.BurndownChartItem

object DatabaseDriver {
    val instance = DatabaseDriverImpl()

    class DatabaseDriverImpl internal constructor() {
        val client = KMongo.createClient()
        val database = client.getDatabase(Constants.DATABASE_NAME)

        private val collections = HashMap<Class<*>, MongoCollection<*>>()

        init {
            registerCollection(database.getCollection(), BurndownChartItem::class.java)
            registerCollection(database.getCollection(), DoneList::class.java)
            registerCollection(database.getCollection(), Member::class.java)
        }


        fun <T : Identifiable> registerCollection(collection: MongoCollection<T>, clazz: Class<T>) {
            collections[clazz] = collection
        }

        fun <T : Identifiable> find(id: String, clazz: Class<T>) : T? {
            return DatabaseHelper.find(id, clazz, collections)
        }

        fun <T : Identifiable> find(field: String, value: String, clazz:Class<T>) : T? {
            return DatabaseHelper.find(field, value, clazz, collections)
        }

        fun <T : Identifiable> save(item: T, clazz: Class<T>) {
            DatabaseHelper.save(item, clazz, collections)
        }

        /*fun saveBurndownChartItem(item: BurndownChartItem) {
            val dbItem = findBurndownChartItem(item.date)
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
            val dbItem = findDoneList(board.id)
            val item = DoneList(board.id, listId)
            if (dbItem == null) {
                doneListCollection.insertOne(item)
            } else {
                doneListCollection.updateOne(Board::id eq dbItem.id, item)
            }
        }

        fun findDoneList(boardId: String): DoneList? {
            return doneListCollection.findOne(DoneList::id eq boardId)
        }*/

        /*fun findMember(id: String): Member? {
            return memberCollection.findOne(Member::id eq id)
        }

        fun saveMembers(items: Collection<Member>) {
            for (item in items) {
                saveMember(item)
            }
        }

        fun saveMember(item: Member) {
            val dbItem = findMember(item.id)
            if (dbItem == null) {
                memberCollection.insertOne(item)
            } else {
                memberCollection.updateOne(Member::id eq item.id, item)
            }
        }*/
    }
}