package nl.teqplay.trelloextension.request

import nl.teqplay.trelloextension.helper.JsonHelper
import com.google.gson.Gson

abstract class BaseTrelloRequest<T> : TrelloRequest<T> {
    val client = JsonHelper.client()
    val gson = Gson()
}