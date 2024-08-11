package app.presentation.usersList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import app.domain.user.User
import app.domain.user.WorkHistory
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.core.screen.Screen
import extensions.generateMonthList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class WorkerHistoryScreen(private val worker: User) : Screen {
    @Composable
    override fun Content() {
        val history = remember { mutableStateMapOf<String, List<WorkHistory>>() }
        val viewModel: WorkerHistoryViewModel = koinInject()
        val months = remember { mutableStateOf(generateMonthList(worker.startingDate))  }

        LaunchedEffect(true) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.historyChange.collectLatest { r ->
                    history.clear()
                    history.putAll(r)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.errorMessages.collectLatest { (message, month) ->
                    months.value.find { it.month == month }?.errorMessage = message
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(modifier = Modifier.padding(top = Dp(20f), bottom = Dp(20f)).align(Alignment.CenterHorizontally),
                    text = "Աշխատողի պատմություն", color = mainGreen, fontSize = TextUnit(16F, TextUnitType.Sp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(months.value) {
                        WorkerHistoryCardView(
                            history[it.month] ?: emptyList(),
                            message = it.errorMessage,
                            workerStartingDate = it.month,
                            onExpand = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.fetchUserHistory(worker.id, it.month)
                                }
                            },
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.loadMore(worker.id, it.month)
                                }
                            })
                    }
                }
            }
        }
    }
}
