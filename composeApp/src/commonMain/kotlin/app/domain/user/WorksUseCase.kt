package app.domain.user

import app.data.work.WorkAssignApi
import dev.gitlive.firebase.database.DataSnapshot
import extensions.endAtKey
import extensions.managerKey
import extensions.requestedAtKey
import extensions.roomNumberKey
import extensions.startedAtKey
import extensions.durationKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorksUseCase(
    private val userProviderUseCase: UserProviderUseCase,
    private val workAssignApi: WorkAssignApi
) {
    private var works = MutableStateFlow<MutableList<WorkData>>(mutableListOf())
    private var allWorks = MutableStateFlow<MutableList<WorkData>>(mutableListOf())
    suspend fun listenToAllWorks(): MutableStateFlow<MutableList<WorkData>> {
        CoroutineScope(Dispatchers.IO).launch {
            workAssignApi.listenToAllWorks().collectLatest { data ->
                val works = mutableListOf<WorkData>()
                data.children.forEach { workersDataSnapshot ->
                    val workerId = workersDataSnapshot.key ?: ""
                    workersDataSnapshot.children.forEach { dataSnapshot ->
                        dataToWorkData(dataSnapshot, workerId)?.let { works.add(it) }
                    }
                }
                allWorks.emit(works)
            }
        }

        return allWorks
    }

    suspend fun listenForWork(): MutableStateFlow<MutableList<WorkData>> {
        CoroutineScope(Dispatchers.IO).launch {
            workAssignApi.listenToWork().collectLatest { data ->
                val currentWorks = mutableListOf<WorkData>()
                data.children.forEach { dataSnapshot ->
                    dataToWorkData(dataSnapshot)?.let { currentWorks.add(it) }
                }
                works.emit(currentWorks)
            }
        }

        return works
    }

    private suspend fun dataToWorkData(
        dataSnapshot: DataSnapshot,
        workerId: String? = null
    ): WorkData? {
        val id = dataSnapshot.key ?: return null
        val managerId = dataSnapshot.child(managerKey).value as? String ?: return null
        val room = dataSnapshot.child(roomNumberKey).value as? String ?: return null
        val duration = dataSnapshot.child(durationKey).value as? Long ?: return null
        val requestedAt = dataSnapshot.child(requestedAtKey).value as? String ?: return null

        val startedAt = dataSnapshot.child(startedAtKey).value as? String
        val endAt = dataSnapshot.child(endAtKey).value as? String
        val manager = userProviderUseCase.getUser(managerId)
        val worker = workerId?.let { userProviderUseCase.getUser(it) }

        return WorkData(
            id = id,
            worker = worker,
            manager = manager,
            room = room,
            duration = duration.toInt(),
            requestedAt = requestedAt,
            startedAt = startedAt,
            endAt = endAt
        )
    }

    suspend fun startWork(workId: String) {
        val currentWork = works.value.firstOrNull {
            it.id == workId
        }
        if (currentWork?.isActive() == true) {
            return
        }
        workAssignApi.startWork(workId)
    }

    private fun currentWork(): WorkData? {
        return works.value.firstOrNull { it.isActive() }
    }

    suspend fun moveWorkToReadyToApprove(workId: String) {
        workAssignApi.moveToReadyToApprove(workId = workId)
    }

    suspend fun approveWork(
        workId: String, date: String, workerId: String,
        numberOfStars: Int,
        message: String
    ) {
        workAssignApi.approveWork(workId, date, workerId, numberOfStars, message)
    }

    suspend fun rejectWork(workId: String, workerId: String) {
        workAssignApi.rejectWork(workId, workerId)
    }
}

data class WorkData(
    val id: String,
    val worker: User?,
    val manager: User,
    val room: String,
    val duration: Int,
    val requestedAt: String,
    val startedAt: String? = null,
    val endAt: String? = null
)

fun WorkData.isActive(): Boolean {
    return startedAt != null && endAt == null
}

fun WorkData.needToApprove(): Boolean {
    return endAt != null
}