package nl.teqplay.trelloextension.mongodb

import com.mongodb.client.MongoCollection
import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.trello.model.DoneList
import nl.teqplay.trelloextension.trello.model.Member
import org.bson.conversions.Bson
import org.litote.kmongo.*
import trello.model.BurndownChartItem

class Database {
    companion object {
        val instance = DatabaseImpl()

        class DatabaseImpl internal constructor() {
            val client = KMongo.createClient()
            val database = client.getDatabase(Constants.DATABASE_NAME)

            private val collections = HashMap<Class<*>, MongoCollection<*>>()

            init {
                registerCollection(database.getCollection(), BurndownChartItem::class.java)
                registerCollection(database.getCollection(), DoneList::class.java)
                registerCollection(database.getCollection(), Member::class.java)
            }


            private fun <T : Identifiable> registerCollection(collection: MongoCollection<T>, clazz: Class<T>) {
                collections[clazz] = collection
            }

            fun <T : Identifiable> find(id: String, clazz: Class<T>) : T? {
                return DatabaseHelper.find(id, clazz, collections)
            }

            fun <T : Identifiable> find(field: String, value: String, clazz:Class<T>) : T? {
                return DatabaseHelper.find(field, value, clazz, collections)
            }

            fun <T : Identifiable> find(parameters: Map<String, String>, clazz: Class<T>) : T? {
                return DatabaseHelper.find(parameters, clazz, collections)
            }

            fun <T : Identifiable> findAll(id: String, clazz: Class<T>) : List<T>? {
                return DatabaseHelper.findAll(id, clazz, collections)
            }

            fun <T : Identifiable> findAll(field: String, value: String, clazz:Class<T>) : List<T>? {
                return DatabaseHelper.findAll(field, value, clazz, collections)
            }

            fun <T : Identifiable> findAll(parameters: Map<String, String>, clazz: Class<T>) : List<T>? {
                return DatabaseHelper.findAll(parameters, clazz, collections)
            }

            fun <T : Identifiable> save(item: T, clazz: Class<T>) {
                DatabaseHelper.save(item, clazz, collections)
            }

            fun <T : Identifiable> saveWhen(item: T, clazz: Class<T>, filter: Bson) {
                DatabaseHelper.save(item, clazz, collections, filter)
            }

            fun <T : Identifiable> delete(item: T, clazz: Class<T>) {
                DatabaseHelper.delete(item, clazz, collections)
            }
        }
    }
}