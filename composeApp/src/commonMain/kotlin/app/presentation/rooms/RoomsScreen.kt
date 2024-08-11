package app.presentation.rooms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.domain.rooms.Room
import app.domain.user.User
import app.domain.user.WorkData
import app.domain.user.isActive
import app.domain.user.needToApprove
import app.presentation.usersList.UserCardView
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import extensions.Timer
import extensions.toYearAndMonth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class RoomsScreen : Screen {

    @Composable
    override fun Content() {
        val timer = Timer()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val rooms = remember { mutableStateListOf<Room>() }
        val activeWorkers = remember { mutableStateListOf<User>() }
        val managers = remember { mutableStateListOf<User>() }
        val works = remember { mutableStateListOf<WorkData>() }
        val invalidate = remember { mutableStateOf(0) }
        val roomsViewModel: RoomViewModel = koinInject()

        LaunchedEffect(true) {
            roomsViewModel.fetchRooms()
            roomsViewModel.fetchUsers()

            CoroutineScope(Dispatchers.IO).launch {
                roomsViewModel.listenForWork().collectLatest { w ->
                    works.clear()
                    works.addAll(w)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                roomsViewModel.roomsFlow.collectLatest { r ->
                    rooms.clear()
                    rooms.addAll(r)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                roomsViewModel.activeWorkersFlow.collectLatest { workers ->
                    activeWorkers.clear()
                    activeWorkers.addAll(workers)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                roomsViewModel.managersFlow.collectLatest { m ->
                    managers.clear()
                    managers.addAll(m)
                }
            }

            timer.start {
                invalidate.value += 1
            }
        }
        Text(text = invalidate.value.toString(), fontSize = TextUnit(1f, TextUnitType.Sp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp),
        ) {
            Text("Օրվա աշխատակիցներ")
            activeUsersView(activeWorkers) {
                roomsViewModel.fetchUsers()
            }
            Text("Սենյակներ")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F, true)
            ) {
                roomView(rooms, bottomSheetNavigator, roomsViewModel)
            }
            Text("Օրվա մենեջերներ")
            managersView(managers) {
                roomsViewModel.fetchUsers()
            }
            Text("Ակտիվ աշխատանքներ")
            currentWorksView(works, timer,
                acceptWork = { workData, numberOfStar, message ->
                    roomsViewModel.acceptWork(
                        workId = workData.id,
                        date = Timer.Now.toYearAndMonth(),
                        workerId = workData.worker?.id ?: "",
                        numberOfStars = numberOfStar, message = message
                    )
                }, rejectWork = { workData ->
                    roomsViewModel.rejectWork(
                        workId = workData.id,
                        workerId = workData.worker?.id ?: ""
                    )
                })
        }
    }

    @Composable
    private fun currentWorksView(
        works: List<WorkData>,
        timer: Timer,
        acceptWork: (WorkData, Int, String) -> Unit,
        rejectWork: (WorkData) -> Unit
    ) {

        LazyRow(
            modifier = Modifier.requiredHeight(90.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(works) { work ->
                WorkCard(
                    workData = work,
                    timer = timer,
                    acceptWork = acceptWork,
                    rejectWork = rejectWork
                )
            }
        }
    }

    @Composable
    private fun managersView(managers: List<User>, onImageChange: () -> Unit) {
        LazyRow(
            modifier = Modifier
                .requiredHeight(90.dp)
        ) {
            items(managers) {
                UserCardView(
                    it,
                    isCompact = true,
                    onChange = {
                        onImageChange()
                    })
            }
        }
    }

    @Composable
    private fun activeUsersView(workers: List<User>, onImageChange: () -> Unit) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(90.dp)
        ) {
            items(workers) {
                UserCardView(
                    it,
                    isCompact = true,
                    onChange = {
                        onImageChange()
                    })
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun roomView(
        rooms: List<Room>,
        bottomSheetNavigator: BottomSheetNavigator,
        roomsViewModel: RoomViewModel
    ) {
        val refreshing by roomsViewModel.isRefreshing

        val pullRefreshState = rememberPullRefreshState(refreshing, { roomsViewModel.fetchRooms() })

        val roomsByHark = rooms.groupBy { it.hark }

        Box(Modifier.pullRefresh(pullRefreshState).fillMaxSize()) {
            Column {
                roomsByHark.keys.sortedDescending().forEach { hark ->
                    Row(
                        Modifier.fillMaxWidth()
                    ){
                        roomsByHark[hark]!!.forEach { room ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(30.dp)

                                    .border(
                                        BorderStroke(1.dp, Color.White),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        bottomSheetNavigator.replaceAll(WorkerChooserScreen(roomsViewModel.activeWorkersFlow.value) {
                                            roomsViewModel.assignWorkTo(
                                                it as User.Worker,
                                                room.number,
                                                room.duration
                                            )
                                            bottomSheetNavigator.hide()
                                        })
                                    }
                                    .background(room.state.color),
                                contentAlignment = Alignment.Center
                            ) {
                                RoomCardView(room)
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun WorkCard(
    workData: WorkData,
    timer: Timer,
    acceptWork: (WorkData, Int, String) -> Unit,
    rejectWork: (WorkData) -> Unit
) {
    val startedAt = workData.startedAt ?: Timer.Now.toString()
    val endAt = workData.endAt ?: Timer.Now.toString()
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    val delayed = timer.calculateDeltaTimeWithMinutes(endAt, startedAt) > workData.duration - 1

    val boarderColor = if (workData.isActive()) {
        Color.Red
    } else if (workData.needToApprove()) {
        Color.Green
    } else {
        Color.Cyan
    }

    if (workData.worker != null) {
        UserCardView(
            workData.worker,
            isCompact = true,
            border = boarderColor,
            onTap = {
                bottomSheetNavigator.show(
                    WorkDetailScreen(
                        workData,
                        timer,
                        acceptWork = { wd, numberOfStar, message ->
                            acceptWork(wd, numberOfStar, message)
                            bottomSheetNavigator.hide()
                        },
                        rejectWork = {
                            rejectWork(it)
                            bottomSheetNavigator.hide()
                        })
                )
            })
    }
}