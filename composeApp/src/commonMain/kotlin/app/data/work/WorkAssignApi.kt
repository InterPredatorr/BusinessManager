@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package app.data.work

import app.data.network.FirebaseBaseNetworkApi
import app.data.network.NetworkResult
import dev.gitlive.firebase.database.DataSnapshot
import extensions.Timer
import extensions.approvedAtKey
import extensions.approvedManagerKey
import extensions.durationKey
import extensions.endAtKey
import extensions.managerKey
import extensions.messageKey
import extensions.numberOfStarsKey
import extensions.requestedAtKey
import extensions.requestedManagerKey
import extensions.roomNumberKey
import extensions.startedAtKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface WorkAssignApi {
    suspend fun assignWork(workerId: String, roomNumber: String, duration: Int): NetworkResult<Unit>

    suspend fun startWork(workId: String): NetworkResult<Unit>

    suspend fun moveToReadyToApprove(workId: String): NetworkResult<Unit>

    suspend fun approveWork(
        workId: String, date: String, workerId: String,
        numberOfStars: Int,
        message: String
    ): NetworkResult<Unit>

    suspend fun rejectWork(workId: String, workerId: String): NetworkResult<Unit>

    fun listenToWork(): Flow<DataSnapshot>

    fun listenToAllWorks(): Flow<DataSnapshot>
}

class WorkAssignFirebaseApi : WorkAssignApi, FirebaseBaseNetworkApi() {
    override suspend fun assignWork(
        workerId: String,
        roomNumber: String,
        duration: Int
    ): NetworkResult<Unit> {
        val data = HashMap<String, Any>()
        data["workerId"] = workerId
        data["roomId"] = roomNumber
        data[requestedAtKey] = Timer.Now.toString()
        data[durationKey] = duration
        return try {
            functions.httpsCallable("assignWork").invoke(data).data<String>()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun startWork(workId: String): NetworkResult<Unit> {
        return try {
            database.reference("works").child(auth.currentUser?.uid ?: "").child(workId)
                .updateChildren(mapOf(startedAtKey to Timer.Now))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun moveToReadyToApprove(workId: String): NetworkResult<Unit> {
        val userId = auth.currentUser?.uid ?: return NetworkResult.Error(Exception("No User"))
        return try {
            database.reference("works").child(userId).child(workId)
                .updateChildren(mapOf(endAtKey to Timer.Now))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun approveWork(
        workId: String,
        date: String,
        workerId: String,
        numberOfStars: Int,
        message: String
    ): NetworkResult<Unit> {
        val userId = auth.currentUser?.uid ?: ""
        return try {
            val data = database.reference("works").child(workerId).child(workId).valueEvents.first()
            val roomNumber = data.child(roomNumberKey).value
            val manager = data.child(managerKey).value
            val requestedAt = data.child(requestedAtKey).value
            val startedAt = data.child(startedAtKey).value
            val endAt = data.child(endAtKey).value

            usersFireStore.document(workerId).collection("works").document(date).collection("Items")
                .add(
                    mapOf(
                        roomNumberKey to roomNumber,
                        requestedManagerKey to manager,
                        startedAtKey to startedAt,
                        requestedAtKey to requestedAt,
                        endAtKey to endAt,
                        approvedAtKey to Timer.Now,
                        approvedManagerKey to userId,
                        numberOfStarsKey to numberOfStars,
                        messageKey to message
                    )
                )
            database.reference("works").child(workerId).child(workId).removeValue()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override suspend fun rejectWork(workId: String, workerId: String): NetworkResult<Unit> {
        return try {
            database.reference("works").child(workerId).child(workId)
                .updateChildren(mapOf(endAtKey to null))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    override fun listenToWork(): Flow<DataSnapshot> {
        return database.reference("works").child(auth.currentUser?.uid ?: "").valueEvents
    }

    override fun listenToAllWorks(): Flow<DataSnapshot> {
        return database.reference("works").valueEvents
    }
}