package extensions

interface Mapper<SOURCE, RESULT> {
    fun map(s: SOURCE): RESULT

    fun map(s: List<SOURCE>): List<RESULT> {
        return s.map { map(it) }
    }
}

interface SuspendMapper<SOURCE, RESULT> {
    suspend fun map(s: SOURCE): RESULT

    suspend fun map(s: List<SOURCE>): List<RESULT> {
        return s.map { map(it) }
    }
}