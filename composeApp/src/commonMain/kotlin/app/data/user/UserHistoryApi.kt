@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")
package app.data.user

import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.startAfter

interface UserHistoryApi {
    suspend fun getUserHistory(userId: String, date: String, count: Int): NetworkResult<List<UserHistoryEntity>>
    suspend fun loadMore(userId: String, date: String, count: Int): NetworkResult<List<UserHistoryEntity>>
}

class UserHistoryFirebaseApi() : UserHistoryApi, FirebaseBaseNetworkApi() {
    private var last: DocumentSnapshot? = null
    override suspend fun getUserHistory(userId: String, date: String, count: Int): NetworkResult<List<UserHistoryEntity>> {
        return try {
            val documents = usersFireStore.document(userId).collection("works").document(date).collection("Items").limit(count).get().documents
            last = documents.last()
            val result = documents.map { doc ->
                doc.data<UserHistoryEntity>()
            }
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    override suspend fun loadMore(userId: String, date: String, count: Int): NetworkResult<List<UserHistoryEntity>> {
        return try {
            last?.let {
                val documents = usersFireStore.document(userId).collection("works").document(date)
                    .collection("Items").startAfter(it).limit(count).get().documents
                last = documents.last()
                val result = documents.map { doc ->
                    doc.data<UserHistoryEntity>()
                }
                NetworkResult.Success(result)
            } ?: getUserHistory(userId, date, count)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}
