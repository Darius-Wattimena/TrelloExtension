package nl.teqplay

import nl.teqplay.request.BaseTrelloRequest
import nl.teqplay.trello.Response
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RequestExecuter {
    companion object {
        private val gson = Gson()
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)

        suspend fun <T> execute(request: BaseTrelloRequest<T>): String {
            return try {
                val result = executeRequest(request)
                processResult(result)
            } catch (cause: Throwable) {
                logger.error("Error occurred while executing a HTTP request", cause)
                val errorResponse = Response()
                errorResponse.error = cause.message
                gson.toJson(errorResponse)
            }
        }


        private suspend fun <T> executeRequest(request: BaseTrelloRequest<T>): T {
            logger.debug("Preparing Request")
            request.prepare()
            logger.debug("Executing Request")
            return request.execute()
        }

        private fun <T> processResult(result: T): String {
            logger.debug("Processing Request Result")
            val response = Response()
            response.value = result
            return gson.toJson(response)
        }
    }
}