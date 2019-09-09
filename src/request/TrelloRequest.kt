package nl.teqplay.request

interface TrelloRequest<T> {
    fun prepare()
    suspend fun execute(): T
}