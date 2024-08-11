package app.presentation.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.rooms.Room
import app.domain.rooms.RoomState
import app.domain.user.User
import app.presentation.login.LoginTextField
import app.presentation.theme.mainGreen
import app.presentation.usersList.UsersListViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import org.koin.compose.koinInject

class RoomDetailScreen(
    private val room: Room,
    val onWorkerChoose: (Int) -> Unit
): Screen {

    @Composable
    override fun Content() {

        val duration = remember { mutableStateOf(room.duration) }
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
        ) {
                Text(
                    room.number.toString(),
                    fontSize = TextUnit(50F, TextUnitType.Sp)
                )
                Text("Ներկա պահին` ${room.state.name}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    LoginTextField(
                        value = if (duration.value == 0) "" else duration.value.toString(),
                        label = "Տևողությունը րոպեներով",
                        keyboardType = KeyboardType.Decimal,
                        onValueChange = {
                            duration.value = try {
                                if (it.isEmpty()) 0 else it.toInt()
                            } catch (e: NumberFormatException) {
                                duration.value
                            }
                        }
                    )
                }
                if (room.state == RoomState.FREE) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = duration.value > 0,
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (duration.value > 0) mainGreen else Color.DarkGray),
                        onClick = {
                            onWorkerChoose(duration.value)
                        }
                    ) {
                        Text("Ընտրել աշխատակից", color = Color.White)
                    }
                }
        }
    }

}