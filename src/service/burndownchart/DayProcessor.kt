package nl.teqplay.trelloextension.service.burndownchart

import com.google.gson.Gson
import io.ktor.client.HttpClient
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BurndownChartDetails
import nl.teqplay.trelloextension.model.BurndownChartItem
import nl.teqplay.trelloextension.model.trello.Card
import nl.teqplay.trelloextension.model.trello.List

class DayProcessor {

    suspend fun process(
        key: String,
        token: String,
        gson: Gson,
        boardCall: TrelloCall,
        client: HttpClient,
        doneListId: String,
        readyListId: String
    ): BurndownChartDetails {
        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)
        val bcDetails = BurndownChartDetails()

        for (list in lists) {
            val listCall = TrelloCall(key, token)
            listCall.request = "/lists/${list.id}/cards"
            listCall.parameters["fields"] = "id,name"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            val hours = getAmount(list, cards, """\[[+-]?(\d*\.)?\d+\]""", "[", "]", doneListId, readyListId, bcDetails)
            val points =
                getAmount(list, cards, """\([+-]?(\d*\.)?\d+\)""", "(", ")", doneListId, readyListId, bcDetails)

            if (list.id == doneListId || list.id == readyListId) {
                bcDetails.donePoints += points.toInt()
                bcDetails.doneItems += cards.size
                bcDetails.doneHoursSpend += hours
            }

            bcDetails.points += points.toInt()
            bcDetails.items += cards.size
            bcDetails.hoursSpend += hours
        }

        return bcDetails
    }

    fun convertToBurndownChartItem(
        boardId: String,
        bcDetails: BurndownChartDetails,
        epochDate: Long
    ): BurndownChartItem {
        return BurndownChartItem(
            boardId,
            epochDate,
            bcDetails.donePoints,
            bcDetails.doneItems,
            bcDetails.doneHoursSpend,
            bcDetails.points,
            bcDetails.items,
            bcDetails.hoursSpend,
            bcDetails.missingInfo
        )
    }

    private fun getAmount(
        list: List,
        cards: Array<Card>,
        regexPattern: String,
        prefix: String,
        suffix: String,
        doneListId: String,
        readyListId: String,
        bcDetails: BurndownChartDetails
    ): Float {
        var resultTotal = 0f
        for (card in cards) {
            val regex = Regex(regexPattern)
            val result = regex.find(card.name)
            if (result != null) {
                val resultString = result.value.removeSurrounding(prefix, suffix)
                val resultValue = resultString.toFloat()
                resultTotal += resultValue
                if (resultValue == 0f && (list.id == doneListId || list.id == readyListId)) {
                    processMissingInfo(card, bcDetails)
                }

            } else {
                processMissingInfo(card, bcDetails)
            }
        }

        return resultTotal
    }

    private fun processMissingInfo(card: Card, bcDetails: BurndownChartDetails) {
        bcDetails.missingInfo[card.id] = true
    }
}