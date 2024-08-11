package app.presentation.usersList

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.data.network.NetworkResult
import app.domain.user.User
import app.domain.user.UserProviderUseCase
import app.domain.user.UserType
import app.presentation.components.Loading.LoadingState
import app.presentation.usersList.SelectedUserTab.MANAGERS
import app.presentation.usersList.SelectedUserTab.WORKERS
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class SelectedUserTab(val raw: Int) {
    WORKERS(0),
    MANAGERS(1)
}

class UsersListViewModel(private val userProviderUseCase: UserProviderUseCase) {

    var users = mutableStateOf<List<User>>(emptyList())
    var activeWorkersFlow = MutableStateFlow<List<User.Worker>>(emptyList())
    var workers = mutableStateOf(emptyList<User.Worker>())
    var managers = mutableStateOf(emptyList<User.Manager>())
    var selectedUserTab = mutableStateOf(WORKERS)
    var currentTabUsers = mutableStateOf(emptyList<User>())

    fun initData() {
        CoroutineScope(Dispatchers.Main).launch {
            val users = userProviderUseCase.getUsers()

            users.forEach { _ ->
                managers.value = users.mapNotNull {
                    it as? User.Manager
                }
                workers.value = users.mapNotNull {
                    it as? User.Worker
                }
            }
            setTabUsers()
        }

    }

    fun getUsersList() {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            users.value = userProviderUseCase.fetchUsers()
            setUsersByType()
            setTabUsers()
            LoadingState.setLoading(false)
        }
    }

    fun setTabUsers() {
        currentTabUsers.value = when (selectedUserTab.value) {
            MANAGERS -> managers.value
            WORKERS -> workers.value
        }
    }

    private fun setUsersByType() {
        workers.value = users.value.filterIsInstance<User.Worker>()
        managers.value = users.value.filterIsInstance<User.Manager>()
    }

    suspend fun addUserImage(image: File, userId: String, completion: (Boolean) -> Unit) {
        LoadingState.setLoading(true)
        when (userProviderUseCase.addImageToFirebaseStorage(image, userId)) {
            is NetworkResult.Success -> {
                completion(true)
            }
            is NetworkResult.Error -> {
                //ToDo show error dialog
                completion(false)
            }
        }

        LoadingState.setLoading(false)
    }

    suspend fun updateUserData(key: String, value: Map<String, Any>, completion: () -> Unit) {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            when (userProviderUseCase.updateUserData(key, value)) {
                is NetworkResult.Success -> LoadingState.setLoading(false)
                else -> {}
            }
            completion()
        }
    }
}