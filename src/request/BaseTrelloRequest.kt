package nl.teqplay.request

import nl.teqplay.helper.JsonHelper
import com.google.gson.Gson

abstract class BaseTrelloRequest<T> : TrelloRequest<T> {
    val client = JsonHelper.client()
    val gson = Gson()
}