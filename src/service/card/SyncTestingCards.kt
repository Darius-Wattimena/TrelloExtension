package nl.teqplay.trelloextension.service.card

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.CardDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.service.BaseRequest

class SyncTestingCards(private val boardId: String, key: String, token: String, private val testingListId: String) : BaseRequest<String>() {
    private val db = Database.instance
    private val call = TrelloCall(key, token)

    override fun prepare() {
        call.request = "/lists/$testingListId/cards"
        call.parameters["members"] = "true"
    }

    override suspend fun execute(): String {
        val resultCards = mutableMapOf<String, Card>()
        val databaseCards = CardDataSource.findAllTestingCards(boardId, testingListId, db)
        val cards = JsonHelper.fromJson(gson, call, client, Array<Card>::class.java)
        val cardsRemovedFromTesting = mutableMapOf<String, Boolean>()

        for (databaseCard in databaseCards) {
            resultCards[databaseCard.id] = databaseCard
            cardsRemovedFromTesting[databaseCard.id] = true
        }

        cards.forEach {
            processCard(it.id, it, resultCards, cardsRemovedFromTesting)
        }

        cardsRemovedFromTesting.filterValues { true }.forEach {
            CardDataSource.removeCard(it.key, db)
        }

        CardDataSource.saveCards(resultCards.values.toMutableList(), boardId, db)

        return Constants.SYNC_SUCCESS_RESPONSE
    }

    fun processCard(cardId: String, card: Card, resultCards: MutableMap<String, Card>, cardsRemovedFromTesting: MutableMap<String, Boolean>) {
        if (resultCards.containsKey(cardId)) {
            resultCards[cardId]!!.daysInList += 1
            cardsRemovedFromTesting[cardId] = false
        } else {
            resultCards[cardId] = card
            resultCards[cardId]!!.daysInList = 1
        }
        resultCards[cardId]!!.boardId = boardId
        resultCards[cardId]!!.listId = testingListId
    }
}