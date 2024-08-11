package app.presentation.rooms

import androidx.compose.runtime.mutableStateOf
import app.data.network.NetworkResult
import app.data.work.WorkAssignApi
import app.domain.rooms.Room
import app.domain.rooms.RoomState
import app.domain.rooms.RoomsProviderUseCase
import app.domain.user.User
import app.domain.user.UserProviderUseCase
import app.domain.user.UserType
import app.domain.user.WorkData
import app.domain.user.WorksUseCase
import app.presentation.components.Loading.LoadingState
import extensions.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RoomViewModel(
    private val roomsProviderUseCase: RoomsProviderUseCase,
    private val workAssignApi: WorkAssignApi,
    private val userProviderUseCase: UserProviderUseCase,
    private val worksUseCase: WorksUseCase
) {

    var roomsFlow = MutableStateFlow<List<Room>>(emptyList())
    var activeWorkersFlow = MutableStateFlow<List<User>>(emptyList())
    var managersFlow = MutableStateFlow<List<User>>(emptyList())
    var isRefreshing = mutableStateOf(false)
    suspend fun listenForWork(): Flow<List<WorkData>> {
        return worksUseCase.listenToAllWorks().map { w ->
            w
        }
    }

    fun fetchUsers() {
        CoroutineScope(Dispatchers.IO).launch {

            val users = userProviderUseCase.fetchUsers()

            val activeUsers = users.filter { user ->
                user.role == UserType.Worker && user.active
            }

            activeWorkersFlow.emit(activeUsers)

            val managers = users.filter { user ->
                user.role == UserType.Manager
            }

            managersFlow.emit(managers)
        }
    }

    fun fetchRooms() {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val rooms = roomsProviderUseCase.provideRooms()

            when (rooms) {
                is NetworkResult.Success -> {
                    roomsFlow.emit(rooms.data)
                }

                is NetworkResult.Error -> {

                }
            }
            LoadingState.setLoading(false)
        }
    }

    fun assignWorkTo(worker: User.Worker, roomNumber: String, duration: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            workAssignApi.assignWork(worker.id, roomNumber, duration)
            roomsFlow.value.find { it.number == roomNumber }?.state = RoomState.CLEANING(worker)
        }
    }

    fun rejectWork(workerId: String, workId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            worksUseCase.rejectWork(workId, workerId)
        }
    }

    fun acceptWork(
        workerId: String,
        date: String,
        workId: String,
        numberOfStars: Int,
        message: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            worksUseCase.approveWork(workId, date, workerId, numberOfStars, message)
        }
    }

}