package nl.teqplay.trelloextension.datasource

import nl.teqplay.trelloextension.model.Card
import org.litote.kmongo.*
import kotlin.collections.toList

object CardDataSource {
    fun findAllTestingCards(boardId: String, testingListId: String, minimumDaysInList: Int, database: Database.Companion.DatabaseImpl) : List<Card> {
        val collection = database.cardCollection
        return collection.find(
            and(
                Card::boardId eq boardId,
                Card::listId eq testingListId,
                Card::daysInList gte minimumDaysInList
            )
        ).toList()
    }

    fun findAllTestingCards(boardId: String, testingListId: String, database: Database.Companion.DatabaseImpl) : List<Card> {
        val collection = database.cardCollection
        return collection.find(
            and(
                Card::boardId eq boardId,
                Card::listId eq testingListId
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
}