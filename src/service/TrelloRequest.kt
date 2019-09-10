package nl.teqplay.trelloextension.service

interface TrelloRequest<T> {
    fun prepare()
    suspend fun execute(): T
}