package app.presentation.login

import androidx.compose.runtime.MutableState
import app.data.network.NetworkResult
import app.domain.login.LoginUseCase
import app.domain.user.User
import app.domain.user.UserProviderUseCase
import app.domain.user.UserType
import app.presentation.components.Loading.LoadingState
import app.presentation.splash.NextAction
import app.presentation.tabs.CurrentTabState
import app.presentation.tabs.RoomsTab
import app.presentation.tabs.WaitWorkTab
import cafe.adriel.voyager.navigator.Navigator
import dev.gitlive.firebase.firestore.FieldValue
import io.ktor.util.valuesOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.reflect.typeOf

class LoginViewModel(val loginUseCase: LoginUseCase, private val userProviderUseCase: UserProviderUseCase) : KoinComponent {

    private var loginSuccess: MutableState<NextAction>? = null
    private var loginError: MutableState<String>? = null

    fun initViewModel(loginSuccess: MutableState<NextAction>, loginError: MutableState<String>) {
        this.loginSuccess = loginSuccess
        this.loginError = loginError
    }

    suspend fun getCurrentUser(): User? {
        return userProviderUseCase.getCurrentUser()
    }

    fun makeUserOnline() {
        CoroutineScope(Dispatchers.IO).launch {
            userProviderUseCase.changeUserStatus(true)
        }
    }

    fun makeUserOffline() {
        CoroutineScope(Dispatchers.IO).launch {
            userProviderUseCase.changeUserStatus(false)
        }
    }

    fun login(name: String, password: String) {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.Default).launch {
            when (val loginResult = loginUseCase.login(name, password)) {
                is NetworkResult.Success -> {
                    val role = loginResult.data.role
                    CurrentTabState.setTab(when (role) {
                        UserType.Worker -> WaitWorkTab
                        else -> RoomsTab
                    })
                    loginSuccess?.value = NextAction.MainScreen(role)
                }

                is NetworkResult.Error -> {
                    loginError?.value = loginResult.exception.message ?: ""
                }
            }
            LoadingState.setLoading(false)
        }
    }

    fun logOut(completion: () -> Unit) {
        LoadingState.setLoading(true)
        CoroutineScope(Dispatchers.Default).launch {
            getCurrentUser()?.let {
                userProviderUseCase.updateUserData(it.id, mapOf("deviceToken" to FieldValue.delete))
            }
            loginUseCase.logOut()
            LoadingState.setLoading(false)
            completion()
        }
    }
}