package nl.teqplay.trelloextension.service

import com.google.gson.Gson
import nl.teqplay.trelloextension.helper.JsonHelper

abstract class BaseTrelloRequest<T> : TrelloRequest<T> {
    val client = JsonHelper.client()
    val gson = Gson()
}