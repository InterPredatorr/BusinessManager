package app.data.cache

interface CacheService<T> {
    suspend fun insert(data: T)

    suspend fun insert(data: List<T>)

    suspend fun getAll(): List<T>

    suspend fun getIf(action: (T) -> Boolean): List<T>

    suspend fun updateUserData(id: String, data: Map<String, Any>)

    suspend fun deleteAll()

    suspend fun deleteIf(action: (T) -> Boolean)
}

