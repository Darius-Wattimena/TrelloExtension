package nl.teqplay.trelloextension.request

interface TrelloRequest<T> {
    fun prepare()
    suspend fun execute(): T
}