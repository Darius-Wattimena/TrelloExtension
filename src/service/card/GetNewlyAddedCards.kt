package nl.teqplay.trelloextension.service.card

import nl.teqplay.trelloextension.datasource.CardDataSource
import nl.teqplay.trelloextension.datasource.Database
import nl.teqplay.trelloextension.helper.TimeHelper
import nl.teqplay.trelloextension.model.trello.Card
import nl.teqplay.trelloextension.service.BaseRequest

class GetNewlyAddedCards(private val boardId: String) : BaseRequest<Array<Card>>() {
    private val db = Database.instance

    override fun prepare() {
    }

    override suspend fun execute(): Array<Card> {
        val today = TimeHelper.getISODateForToday()
        return CardDataSource.findAllNewCardsOfAllLists(boardId, today, db).toTypedArray()
    }
}