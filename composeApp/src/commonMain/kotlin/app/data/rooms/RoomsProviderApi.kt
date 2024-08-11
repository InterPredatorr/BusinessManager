package app.data.rooms

import app.data.network.NetworkResult
import app.domain.rooms.RoomState.Companion.roomBusyState
import app.domain.rooms.RoomState.Companion.roomFreeState
import app.domain.rooms.RoomState.Companion.roomPayingState
import extensions.roomNumberKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RoomsProviderApi {
    suspend fun provideRoomsState(): NetworkResult<List<RoomsStateEntity>>
}

class RoomProviderC1Api : RoomsProviderApi {
    override suspend fun provideRoomsState(): NetworkResult<List<RoomsStateEntity>> {
        val results = mutableListOf(
            RoomsStateEntity("509", generateRandomState(), 8, 5),
            RoomsStateEntity("508", generateRandomState(), 8, 5),
            RoomsStateEntity("507", generateRandomState(), 8, 5),
            RoomsStateEntity("506", generateRandomState(), 7, 5),
            RoomsStateEntity("505", generateRandomState(), 7, 5),
            RoomsStateEntity("504", generateRandomState(), 8, 5),
            RoomsStateEntity("503", generateRandomState(), 8, 5),
            RoomsStateEntity("502", generateRandomState(), 8, 5),
            RoomsStateEntity("501", generateRandomState(), 7, 5),
            RoomsStateEntity("412", generateRandomState(), 7, 4),
            RoomsStateEntity("411", generateRandomState(), 7, 4),
            RoomsStateEntity("410", generateRandomState(), 7, 4),
            RoomsStateEntity("409", generateRandomState(), 7, 4),
            RoomsStateEntity("407", generateRandomState(), 8, 4),
            RoomsStateEntity("405", generateRandomState(), 7, 4),
            RoomsStateEntity("404", generateRandomState(), 8, 4),
            RoomsStateEntity("403", generateRandomState(), 8, 4),
            RoomsStateEntity("402", generateRandomState(), 8, 4),
            RoomsStateEntity("401", generateRandomState(), 7, 4),
            RoomsStateEntity("310", generateRandomState(), 8, 3),
            RoomsStateEntity("309", generateRandomState(), 8, 3),
            RoomsStateEntity("305", generateRandomState(), 7, 3),
            RoomsStateEntity("304", generateRandomState(), 8, 3),
            RoomsStateEntity("303", generateRandomState(), 8, 3),
            RoomsStateEntity("302", generateRandomState(), 8, 3),
            RoomsStateEntity("301", generateRandomState(), 7, 3),
            RoomsStateEntity("207", generateRandomState(), 7, 2),
            RoomsStateEntity("206", generateRandomState(), 7, 2),
            RoomsStateEntity("205", generateRandomState(), 7, 2),
            RoomsStateEntity("204", generateRandomState(), 7, 2),
            RoomsStateEntity("203", generateRandomState(), 7, 2),
            RoomsStateEntity("202", generateRandomState(), 7, 2),
            RoomsStateEntity("201", generateRandomState(), 7, 2),
            RoomsStateEntity("108", generateRandomState(), 8, 1),
            RoomsStateEntity("105", generateRandomState(), 7, 1),
            RoomsStateEntity("104", generateRandomState(), 7, 1),
            RoomsStateEntity("103", generateRandomState(), 7, 1),
            RoomsStateEntity("102", generateRandomState(), 7, 1),
            RoomsStateEntity("101", generateRandomState(), 7, 1),
            RoomsStateEntity("VIP 1", generateRandomState(), 10, 0),
            RoomsStateEntity("VIP 2", generateRandomState(), 10, 0),
            RoomsStateEntity("VIP 3", generateRandomState(), 5, 0),
            RoomsStateEntity("VIP 4", generateRandomState(), 5, 0),
            RoomsStateEntity("VIP 5", generateRandomState(), 10, 0),
            RoomsStateEntity("VIP 6", generateRandomState(), 10, 0),
            RoomsStateEntity("VIP 7", generateRandomState(), 15, 0),
            RoomsStateEntity("VIP 8", generateRandomState(), 10, 0),
            RoomsStateEntity("VIP 9", generateRandomState(), 25, -2),
            RoomsStateEntity("VIP 10", generateRandomState(), 25, -1),
            RoomsStateEntity("VIP 11", generateRandomState(), 25, -1),
            RoomsStateEntity("VIP 12", generateRandomState(), 25, -1),
            RoomsStateEntity("VIP 14", generateRandomState(), 20, -2),
            RoomsStateEntity("VIP 15", generateRandomState(), 20, -1),
            RoomsStateEntity("VIP 16", generateRandomState(), 20, -1),


            )

        return NetworkResult.Success(results)
    }


    private fun generateRandomState(): String {
        return roomFreeState;
        /*  return when ((0..3).random()) {
              0 -> roomBusyState
              1 -> roomFreeState
              else -> roomPayingState
          }*/
    }
}

@Serializable
data class RoomsStateEntity(
    @SerialName(roomNumberKey) val roomNumber: String,
    @SerialName("state") val roomState: String,
    @SerialName("duration") val duration: Int = 20,
    @SerialName("hark") val hark: Int = 20
)