package nl.teqplay.trelloextension.service

import com.google.gson.Gson
import nl.teqplay.trelloextension.helper.JsonHelper

abstract class BaseRequest<T> : Request<T> {
    val client = JsonHelper.client()
    val gson = Gson()
}