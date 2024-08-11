package app.presentation.usersList

import app.data.network.NetworkResult
import app.domain.user.WorkHistory
import app.domain.user.WorkerHistoryUseCase
import app.presentation.components.Loading.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkerHistoryViewModel(private val historyUseCase: WorkerHistoryUseCase) {
    var historyChange = MutableStateFlow(mutableMapOf<String, List<WorkHistory>>())
    var errorMessages = MutableSharedFlow<Pair<String, String>>()

    suspend fun fetchUserHistory(userId: String, date: String, count: Int = 2) {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val result = historyUseCase.provideUserHistory(userId, date, count)
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data.isEmpty()) {
                        errorMessages.emit(Pair("Աշխատողը պատմություն չունի", date))
                    } else {
                        historyChange.value = historyChange.value.toMutableMap().apply {
                            this[date] = result.data
                            historyChange.emit(this)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    if (result.exception.message?.contains("List is empty") ?: false) {
                        errorMessages.emit(Pair("Աշխատակիցը պատմություն չունի այս ամիս", date))
                    } else {
                        errorMessages.emit(Pair("Սխալ՝ պատմությունը բեռնելիս", date))
                    }
                }
            }
            LoadingState.setLoading(false)
        }
    }

    suspend fun loadMore(userId: String, date: String, count: Int = 2) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = historyUseCase.loadMore(userId, date, count)
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data.isEmpty()) {
                        errorMessages.emit(Pair("Աշխատողը այլ պատմություն չունի", date))
                    } else {
                        var newList = historyChange.value[date]
                        newList?.plus(result.data)?.let {
                            historyChange.value = historyChange.value.toMutableMap().apply {
                                this[date] = it
                                historyChange.emit(this)
                            }
                        }
                    }
                }
                is NetworkResult.Error -> {
                    errorMessages.emit(Pair("Աշխատողը այլ պատմություն չունի", date))
                }
            }
        }
    }
}