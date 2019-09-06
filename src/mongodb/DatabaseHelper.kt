package nl.teqplay.mongodb

import com.mongodb.client.MongoCollection
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.save

class DatabaseHelper {
    companion object {
        fun <T : Identifiable> getCollection(clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>) : MongoCollection<T> {
            return collections[clazz] as MongoCollection<T>
        }

        fun <T : Identifiable> find(id: String, clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>) : T? {
            val collection = getCollection(clazz, collections)
            return collection.findOne(Identifiable::_id eq id)
        }

        fun <T : Identifiable> find(field: String, value: String, clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>) : T? {
            val collection = getCollection(clazz, collections)
            return collection.findOne("{$field: $value}")
        }

        fun <T : Identifiable> save(item: T, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>) {
            val collection = getCollection(clazz, collections)
            return collection.save(item)
        }
    }
}