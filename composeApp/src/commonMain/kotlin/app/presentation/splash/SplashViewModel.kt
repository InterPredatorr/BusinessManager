package app.presentation.splash

import app.domain.user.UserProviderUseCase
import app.domain.user.UserType

class SplashViewModel(val userProviderUseCase: UserProviderUseCase) {
    suspend fun setup(): SplashResult {
        val currentUser = userProviderUseCase.getCurrentUser()

        userProviderUseCase.fetchUsers()

        return SplashResult(currentUser != null, currentUser?.role ?: UserType.Worker)
    }
}

data class SplashResult(val isLoggedIn: Boolean, val userType: UserType)