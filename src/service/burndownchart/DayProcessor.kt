package nl.teqplay.trelloextension.service.burndownchart

import com.google.gson.Gson
import io.ktor.client.HttpClient
import nl.teqplay.trelloextension.helper.JsonHelper
import nl.teqplay.trelloextension.helper.RequestInfo
import nl.teqplay.trelloextension.helper.TrelloCall
import nl.teqplay.trelloextension.model.BurndownChartDetails
import nl.teqplay.trelloextension.model.BurndownChartItem
import nl.teqplay.trelloextension.model.Card
import nl.teqplay.trelloextension.model.List

class DayProcessor() {

    private val bcDetails = BurndownChartDetails()

    suspend fun process(
        key: String,
        token: String,
        gson: Gson,
        boardCall: TrelloCall,
        client: HttpClient,
        doneListId: String
    ): BurndownChartDetails {
        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)

        for (list in lists) {
            val listCall = TrelloCall(key, token)
            listCall.request = "/lists/${list.id}/cards"
            listCall.parameters["fields"] = "id,name"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            val hours = getAmount(list, cards, """\[[+-]?(\d*\.)?\d+\]""", "[", "]", doneListId)
            val points = getAmount(list, cards, """\([+-]?(\d*\.)?\d+\)""", "(", ")", doneListId)

            if (list.id == doneListId) {
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

    fun convertToBurndownChartItem(bcDetails: BurndownChartDetails, epochDate: Long): BurndownChartItem {
        return BurndownChartItem(
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
        doneListId: String
    ): Float {
        var resultTotal = 0f
        for (card in cards) {
            val regex = Regex(regexPattern)
            val result = regex.find(card.name)
            if (result != null) {
                val resultString = result.value.removeSurrounding(prefix, suffix)
                val resultValue = resultString.toFloat()
                resultTotal += resultValue
                // TODO fix logic
                if (resultValue == 0f && list.id == doneListId) {
                    processZeroHoursOnDoneItem(card, resultString)
                }

            } else {
                processMissingInfo(card)
            }
        }

        return resultTotal
    }

    private fun processMissingInfo(card: Card) {
        bcDetails.missingInfo[card.id] = true
    }

    private fun processZeroHoursOnDoneItem(card: Card, result: String) {
        if (result.toFloat() == 0f) {
            processMissingInfo(card)
        }
    }
}