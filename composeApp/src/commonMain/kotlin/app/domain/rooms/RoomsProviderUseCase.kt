package app.domain.rooms

import androidx.compose.ui.graphics.Color
import app.data.network.NetworkResult
import app.data.rooms.RoomsProviderApi
import app.data.work.CurrentWorksApi
import app.domain.user.User
import app.domain.user.UserProviderUseCase

interface RoomsProviderUseCase {
    suspend fun provideRooms(): NetworkResult<List<Room>>
}

class RoomsProviderCombineUseCase(private val roomsProviderApi: RoomsProviderApi, private val currentWorksApi: CurrentWorksApi, private val userProviderUseCase: UserProviderUseCase) : RoomsProviderUseCase {
    override suspend fun provideRooms(): NetworkResult<List<Room>> {
        val roomsResult = roomsProviderApi.provideRoomsState()
        return when (roomsResult) {
            is NetworkResult.Success -> {
                val rooms = roomsEntityToRoomsMapper.map(roomsResult.data)

                when (val currentWorks = currentWorksApi.provideCurrentWorks()) {
                    is NetworkResult.Success -> {
                        currentWorks.data.forEach { work ->
                            rooms.firstOrNull { room ->
                                room.number==work.room
                            }?.let {
                                val worker = userProviderUseCase.getUser(work.worker)
                                it.state = RoomState.CLEANING(worker)
                            }
                        }

                        NetworkResult.Success(rooms)
                    }

                    is NetworkResult.Error -> {
                        NetworkResult.Error(currentWorks.exception)
                    }
                }
            }

            is NetworkResult.Error -> {
                NetworkResult.Error(roomsResult.exception)
            }
        }
    }

}

data class Room(val number: String, var state: RoomState, val duration: Int, val hark: Int) {
    val isCleaning: Boolean
        get() = this.state is RoomState.CLEANING

    val isDirty: Boolean
        get() = this.state is RoomState.DIRTY
}

sealed class RoomState {
    data object FREE : RoomState()
    data object BUSY : RoomState()
    data class CLEANING(val worker: User) : RoomState()
    data object PAYING : RoomState()
    data object DIRTY : RoomState()

    val color: Color
        get() = when (this) {
            is FREE -> Color.Red
            is BUSY -> Color.Blue
            is CLEANING -> Color.Green
            is PAYING -> Color.Blue
            is DIRTY -> Color.Green
        }

    val name: String
        get() = when (this) {
            is FREE -> "Ազատ է"
            is BUSY -> "Զբաղված է"
            is CLEANING -> "Մաքրվում է"
            is PAYING -> "Վճարվում է"
            is DIRTY -> "Կեղտոտ է"
        }

    val id: String
        get() = when (this) {
            is FREE -> roomFreeState
            is BUSY -> roomBusyState
            is CLEANING -> roomCleaningState
            is PAYING -> roomPayingState
            is DIRTY -> roomDirtyState
        }

    fun String.toRoomState(): RoomState? {
        return when (this) {
            roomFreeState -> FREE
            roomBusyState -> BUSY
            roomPayingState -> PAYING
            else -> null
        }
    }

    companion object {
        const val roomBusyState = "busy"
        const val roomFreeState = "free"
        const val roomPayingState = "paying"
        const val roomDirtyState = "dirty"
        const val roomCleaningState = "cleaning"
    }
}