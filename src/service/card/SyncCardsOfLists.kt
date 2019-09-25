package nl.teqplay.trelloextension.service.card

import nl.teqplay.trelloextension.Constants
import nl.teqplay.trelloextension.datasource.CardDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.trello.Card
import nl.teqplay.trelloextension.model.trello.List
import nl.teqplay.trelloextension.service.BaseRequest

class SyncCardsOfLists(
    private val boardId: String,
    private val key: String,
    private val token: String,
    private val lists: Array<List>,
    private val stringToday: String
) : BaseRequest<String>() {
    private val db = Database.instance

    override fun prepare() {
    }

    override suspend fun execute(): String {
        val resultCards = mutableMapOf<String, Card>()

        val listIds = mutableListOf<String>()

        for (list in lists) {
            listIds.add(list.id)
        }

        val databaseCards = CardDataSource.findAllCardsOfList(boardId, listIds.toTypedArray(), db)

        val cardsRemovedFromOldList = mutableMapOf<String, Boolean>()

        for (databaseCard in databaseCards) {
            resultCards[databaseCard.id] = databaseCard
            cardsRemovedFromOldList[databaseCard.id] = true
        }


        for (listId in listIds) {
            val call = TrelloCall(key, token)
            call.request = "/lists/$listId/cards"
            call.parameters["members"] = "true"

            val cards = JsonHelper.fromJson(gson, call, client, Array<Card>::class.java)

            for (card in cards) {
                processCard(card.id, card, resultCards, cardsRemovedFromOldList, listId, stringToday)
            }
        }

        cardsRemovedFromOldList.filterValues { it }.forEach {
            CardDataSource.removeCard(it.key, db)
        }

        CardDataSource.saveCards(resultCards.values.toMutableList(), boardId, db)

        return Constants.SYNC_SUCCESS_RESPONSE
    }

    private fun processCard(
        cardId: String,
        card: Card,
        resultCards: MutableMap<String, Card>,
        cardsRemovedFromTesting: MutableMap<String, Boolean>,
        listId: String,
        stringToday: String
    ) {
        var currentCardDaysInList = 1
        if (resultCards.containsKey(cardId)) {
            cardsRemovedFromTesting[cardId] = false

            card.dateAdded = resultCards[cardId]!!.dateAdded
            card.boardId = resultCards[cardId]!!.boardId

            if (resultCards[cardId]!!.listId == listId) {
                currentCardDaysInList = resultCards[cardId]!!.daysInList + 1
            }
        } else {
            card.dateAdded = stringToday
            card.boardId = boardId
        }

        card.daysInList = currentCardDaysInList
        card.listId = listId
        resultCards[cardId] = card
    }
}