package nl.teqplay

interface TrelloRequest<T> {
    fun prepare()
    suspend fun execute(): T
}