package app.presentation.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.User
import app.presentation.theme.mainGreen
import app.presentation.usersList.UserSelectableView
import app.presentation.usersList.UsersListViewModel
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject

class WorkerChooserScreen(
    private val workers: List<User>,
    private val buttonTitle: String = "Ուղարկել",
    val completion: (User) -> Unit
) : Screen {
    @Composable
    override fun Content() {

        val selectedWorker: MutableState<User?> = remember { mutableStateOf(null) }

        Column(
            modifier = Modifier
                .fillMaxHeight(0.9F)
                .padding(vertical = 5.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(20.dp),
                content = {
                    Text(
                        buttonTitle,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = TextUnit(12F, TextUnitType.Sp)
                    )
                },
                enabled = selectedWorker.value!=null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = mainGreen),
                onClick = {
                    completion(selectedWorker.value!!)
                }
            )
            LazyColumn {
                items(workers) { worker ->
                    UserSelectableView(
                        worker,
                        isSelected = worker.id == (selectedWorker.value?.id ?: ""),
                        onSelect = {
                            selectedWorker.value = worker
                        },
                        onImageChange = {

                        }
                    )
                }
            }
        }
    }
}