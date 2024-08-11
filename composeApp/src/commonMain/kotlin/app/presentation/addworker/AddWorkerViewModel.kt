package app.presentation.addworker

import app.data.network.NetworkResult
import app.domain.login.LoginUseCase
import app.domain.user.UserType
import app.presentation.components.Loading.LoadingState
import app.presentation.tabs.CurrentTabState
import app.presentation.tabs.ManageWorkersTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AddWorkerViewModel(val loginUseCase: LoginUseCase) {
    var addWorkerResult = MutableSharedFlow<Boolean>()

    fun addWorker(name: String, password: String, age: Int, userType: UserType) {
        CoroutineScope(Dispatchers.IO).launch {
            LoadingState.setLoading(true)
            val result = loginUseCase.createNewWorkerAccount(name, password, age, userType)
            when (result) {
                is NetworkResult.Success -> {
                    addWorkerResult.emit(true)
                    CurrentTabState.setTab(ManageWorkersTab)
                }

                is NetworkResult.Error -> {
                    addWorkerResult.emit(false)
                }
            }
            LoadingState.setLoading(false)
            CurrentTabState.setTab(ManageWorkersTab)
        }
    }
}