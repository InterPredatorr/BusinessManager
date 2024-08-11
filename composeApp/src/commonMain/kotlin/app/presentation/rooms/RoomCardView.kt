package app.presentation.rooms

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import app.domain.rooms.Room


@Composable
fun RoomCardView(room: Room) {
    Text(
        room.number,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        fontSize = TextUnit(16F, TextUnitType.Sp),
        color = Color.White
    )
}

