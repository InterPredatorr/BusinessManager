package app.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import app.domain.user.UserType
import app.presentation.tabs.CurrentTabState
import app.presentation.tabs.RoomsTab
import app.presentation.tabs.WaitWorkTab
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.navigator.tab.Tab
import extensions.scaledSp
import org.koin.compose.koinInject

@Composable
fun splashView(nextAction: MutableState<NextAction>) {
    val splashViewModel: SplashViewModel = koinInject()

    LaunchedEffect(key1 = true) {
        val splashResult = splashViewModel.setup()

        if (splashResult.isLoggedIn) {
            CurrentTabState.setTab(initialTab(splashResult.userType))
            nextAction.value = NextAction.MainScreen(splashResult.userType)
        } else {
            nextAction.value = NextAction.LoginAction
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text ="Իսրայել Օրի",
            color = mainGreen,
            fontWeight = FontWeight.Black,
            fontSize = 50F.scaledSp()
            )
    }
}

private fun initialTab(type: UserType): Tab {
    return when (type) {
        UserType.Owner, UserType.Manager -> RoomsTab
        UserType.Worker -> WaitWorkTab
    }
}

sealed class NextAction {
    data object Splash : NextAction()
    data object LoginAction : NextAction()
    data class MainScreen(val userType: UserType) : NextAction()
}