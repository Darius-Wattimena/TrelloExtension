package nl.teqplay.request.burndownchart

import nl.teqplay.helper.JsonHelper
import nl.teqplay.helper.Request
import nl.teqplay.helper.TrelloCall
import nl.teqplay.trello.model.Card
import nl.teqplay.trello.model.List
import com.google.gson.Gson
import io.ktor.client.HttpClient
import trello.model.BurndownChartItem

class DayProcessor(
    val request: Request,
    private val doneListId: String) {

    private val bcDetails = BurndownChartDetails()

    suspend fun process(request: Request, gson: Gson, boardCall: TrelloCall, client: HttpClient) : BurndownChartDetails {
        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)

        for (list in lists) {
            val listCall = TrelloCall(request.GetKey(), request.GetToken())
            listCall.request = "/lists/${list.id}/cards"
            listCall.parameters["fields"] = "id,name"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            val point = getAmount(list, cards, """\[[+-]?(\d*\.)?\d+\]""", "[", "]")
            val hours = getAmount(list, cards, """\([+-]?(\d*\.)?\d+\)""", "(", ")")

            if (list.id == doneListId) {
                bcDetails.donePoint += point.toInt()
                bcDetails.doneItems += cards.size
                bcDetails.doneHoursSpend += hours
            }

            bcDetails.point += point.toInt()
            bcDetails.items += cards.size
            bcDetails.hoursSpend += hours
        }

        return bcDetails
    }

    fun convertToBurndownChartItem(bcDetails: BurndownChartDetails, epochDate: Long) : BurndownChartItem {
        return BurndownChartItem("",
            epochDate,
            bcDetails.donePoint,
            bcDetails.doneItems,
            bcDetails.doneHoursSpend,
            bcDetails.point,
            bcDetails.items,
            bcDetails.hoursSpend,
            bcDetails.missingInfo
        )
    }

    private fun getAmount(list: List, cards: Array<Card>, regexPattern: String, prefix: String, suffix: String) : Float {
        var resultTotal = 0f
        for (card in cards) {
            val regex = Regex(regexPattern)
            val result = regex.find(card.name)
            if (result != null) {
                val resultString = result.value.removeSurrounding(prefix, suffix)
                val resultValue = resultString.toFloat()
                resultTotal += resultValue
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