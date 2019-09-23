package nl.teqplay.trelloextension.service

interface Request<T> {
    fun prepare()
    suspend fun execute(): T
}