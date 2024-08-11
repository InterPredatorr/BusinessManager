package app.domain.rooms

import app.data.rooms.RoomsStateEntity
import app.domain.rooms.RoomState.BUSY.toRoomState
import extensions.Mapper

val roomsEntityToRoomsMapper = object : Mapper<RoomsStateEntity, Room> {
    override fun map(s: RoomsStateEntity): Room {
        return Room(s.roomNumber, s.roomState.toRoomState() ?: RoomState.FREE, s.duration, s.hark)
    }

}