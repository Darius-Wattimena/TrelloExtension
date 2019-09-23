package nl.teqplay.trelloextension

import com.google.gson.Gson
import nl.teqplay.trelloextension.service.BaseRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestExecuter {
    companion object {
        private val gson = Gson()
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)

        suspend fun <T> execute(request: BaseRequest<T>): String {
            val result = executeRequest(request)
            return processResult(result)
        }


        private suspend fun <T> executeRequest(request: BaseRequest<T>): T {
            logger.debug("Preparing Request")
            request.prepare()
            logger.debug("Executing Request")
            return request.execute()
        }

        private fun <T> processResult(result: T): String {
            logger.debug("Processing Request Result")
            return gson.toJson(result)
        }
    }
}