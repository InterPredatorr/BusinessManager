package app.presentation.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.user.WorkData
import app.domain.user.isActive
import app.presentation.components.Loading.LoadingState
import app.presentation.theme.mainGreen
import app.presentation.usersList.multicolorText
import cafe.adriel.voyager.core.screen.Screen
import extensions.Timer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

class WorkerScreen : Screen, KoinComponent {
    @Composable
    override fun Content() {
        val works = remember { mutableStateListOf<WorkData>() }
        val invalidate = remember { mutableStateOf(0) }
        val viewModel: WorkerScreenViewModel = koinInject()

        LaunchedEffect(true) {
            launch {
                LoadingState.setLoading(true)
                viewModel.listenForWork().collectLatest { data ->
                    works.clear()
                    works.addAll(data)
                    LoadingState.setLoading(false)
                }
            }

            viewModel.timer.start {
                invalidate.value += 1
            }
        }

        val hasCurrentWork = works.firstOrNull { it.isActive() }!=null
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = invalidate.value.toString(), fontSize = TextUnit(0f, TextUnitType.Sp))
                if (works.isEmpty()) {
                    Text(
                        "Ներկա պահին աշխատանք չկա",
                        color = Color.Black,
                        fontSize = TextUnit(36f, TextUnitType.Sp),
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(Dp(20f))
                    )
                } else {
                    LazyVerticalGrid(
                        GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(works) { work ->
                            WorkCard(
                                workData = work,
                                timer = viewModel.timer,
                                showAcceptButton = !hasCurrentWork,
                                acceptWork = { workData ->
                                    viewModel.startWork(workData.id)
                                },
                                finishWork = { workData ->
                                    viewModel.moveWorkToReadyToApprove(workData.id)
                                })
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WorkCard(workData: WorkData, showAcceptButton: Boolean, timer: Timer, acceptWork: (WorkData) -> Unit, finishWork: (WorkData) -> Unit) {
        val buttonText = workData.startedAt?.let { "Ավարտել" } ?: "Ընդունել"
        val clickAction = workData.startedAt?.let { finishWork } ?: acceptWork
        val requestedAt = workData.requestedAt
        val startedAt = workData.startedAt ?: Timer.Now.toString()
        val endAt = workData.endAt ?: Timer.Now.toString()
        val delayed = timer.calculateDeltaTimeWithMinutes(endAt, startedAt) > workData.duration - 1

        val fontSize = 24f

        val showButton = workData.startedAt?.let { true } ?: showAcceptButton
        Column(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .padding(12.dp)
                .border(BorderStroke(1.dp, SolidColor(if (delayed) mainGreen else Color.Red)),
                    RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    ("Մենեջեր՝ " + workData.manager.name),
                    color = Color.Black,
                    fontSize = TextUnit(fontSize, TextUnitType.Sp),
                )
                multicolorText(
                    "Սենյակ՝ " + workData.room,
                    if (delayed) "(Ուշացում)" else "",
                    firstColor = if (delayed) mainGreen else Color.Red,
                    secondColor = mainGreen,
                    fontSize = if (delayed) fontSize else fontSize + 6,
                    spaceBetween = true
                )
                Text(
                    "Հանձնարարվել է՝ " + timer.calculateTime(requestedAt) + " ր առաջ",
                    color = if (delayed) mainGreen else Color.Red,
                    fontSize = TextUnit(fontSize, TextUnitType.Sp),
                )
                Text("Տևողություն՝ " + workData.duration + " ր", fontSize = TextUnit(fontSize, TextUnitType.Sp))
                workData.startedAt?.let { startedAt ->
                    Text(
                        "Անցել է՝ " + timer.calculateTime(startedAt) + " ր",
                        color = if (delayed) mainGreen else Color.Red,
                        fontSize = TextUnit(fontSize, TextUnitType.Sp),
                    )
                }
            }

            if (showButton) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (delayed) mainGreen else Color.LightGray),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    onClick = {
                        clickAction.invoke(workData)
                    }
                ) {
                    Text(
                        buttonText,
                        color = Color.Black
                    )
                }
            }
        }
    }
}