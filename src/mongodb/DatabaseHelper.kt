package nl.teqplay.trelloextension.mongodb

import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import org.litote.kmongo.*
import kotlin.collections.toList

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

        fun <T : Identifiable> find(parameters: Map<String, String>, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>): T? {
            val collection = getCollection(clazz, collections)
            val parametersString = parameters.map { (field, value) -> "$field: $value" }.joinToString { ", " }
            return collection.findOne("{$parametersString}")
        }

        fun <T : Identifiable> findAll(id: String, clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>) : List<T>? {
            val collection = getCollection(clazz, collections)
            return collection.find(Identifiable::_id eq id).toList()
        }

        fun <T : Identifiable> findAll(bson: Bson, clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>) : List<T>? {
            val collection = getCollection(clazz, collections)
            return collection.find(bson).toList()
        }

        fun <T : Identifiable> findAll(parameters: Map<String, String>, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>): List<T>? {
            val collection = getCollection(clazz, collections)
            val parametersString = parameters.map { (field, value) -> "$field: $value" }.joinToString { ", " }
            return collection.find("{$parametersString}").toList()
        }

        fun <T : Identifiable> findAll(clazz: Class<T>, collections: HashMap<Class<*>, MongoCollection<*>>): List<T>? {
            val collection = getCollection(clazz, collections)
            return collection.find().toList()
        }

        fun <T : Identifiable> save(item: T, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>) {
            val collection = getCollection(clazz, collections)
            if (item._id != null && item._id!!.isNotBlank()) {
                collection.updateOne(Identifiable::_id eq item._id, item)
            } else {
                item._id = null
                collection.insertOne(item)
            }
        }

        fun <T : Identifiable> saveWhen(item: T, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>, filter: Bson) {
            val collection = getCollection(clazz, collections)
            val result = collection.updateOne(filter, item)
            if (result.matchedCount == 0L) {
                collection.insertOne(item)
            }
        }

        fun <T : Identifiable> saveOnly(item: T, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>, filter: Bson) {
            val collection = getCollection(clazz, collections)
            val result = collection.updateOne(filter, item)
            if (result.modifiedCount == 0L) {
                collection.insertOne(item)
            }
        }

        fun <T : Identifiable> delete(item: T, clazz: Class<T>, collections: java.util.HashMap<Class<*>, MongoCollection<*>>) {
            val collection = getCollection(clazz, collections)
            collection.deleteOneById(item._id!!)
        }
    }
}