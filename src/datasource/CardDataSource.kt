package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.trello.Card
import org.bson.conversions.Bson
import org.litote.kmongo.*
import kotlin.collections.toList

object CardDataSource {
    fun findAllCardsOfList(boardId: String, listId: String, minimumDaysInList: Int, database: Database.Companion.DatabaseImpl) : List<Card> {
        val collection = database.cardCollection
        return collection.find(
            and(
                Card::boardId eq boardId,
                Card::listId eq listId,
                Card::daysInList gte minimumDaysInList
            )
        ).toList()
    }

    fun findAllCardsOfList(boardId: String, listIds: Array<String>, database: Database.Companion.DatabaseImpl) : List<Card> {
        val collection = database.cardCollection
        val listsSelect = mutableListOf<Bson>()

        for (listId in listIds) {
            listsSelect.add(Card::listId eq listId)
        }
        return collection.find(
            and(
                Card::boardId eq boardId,
                or(listsSelect)
            )
        ).toList()
    }



    fun saveCard(card: Card, boardId: String, database: Database.Companion.DatabaseImpl) {
        val collection = database.cardCollection
        val updateResult = collection.updateOne(
            and(
                Card::id eq card.id,
                Card::boardId eq boardId
            ), card)

        if (updateResult.matchedCount == 0L) {
            collection.insertOne(card)
        }
    }

    fun saveCards(cards: MutableList<Card>, boardId: String, database: Database.Companion.DatabaseImpl) {
        for (card in cards) {
            saveCard(card, boardId, database)
        }
    }

    fun removeCard(cardId: String, database: Database.Companion.DatabaseImpl) {
        val collection = database.cardCollection
        collection.deleteOne(
            Card::id eq cardId
        )
    }

    fun findAllNewCardsOfAllLists(
        boardId: String,
        todayDate: String,
        database: Database.Companion.DatabaseImpl
    ) : List<Card> {
        val collection = database.cardCollection
        return collection.find(
            and(
                Card::boardId eq boardId,
                Card::dateAdded eq todayDate
            )
        ).toList()
    }
}