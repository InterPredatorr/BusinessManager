package app.presentation.components.Loading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.domain.user.UserType
import app.presentation.login.LoginScreen
import app.presentation.main.mainScreen
import app.presentation.splash.NextAction
import app.presentation.splash.splashView
import app.presentation.tabs.CurrentTabState
import app.presentation.tabs.RoomsTab
import app.presentation.tabs.WaitWorkTab
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun LoadingView() {
    val isLoading by LoadingState.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isLoading) Color.Black.copy(alpha = 0.5F) else Color.Transparent)
            .clickable(
                indication = null, // disable ripple effect
                interactionSource = remember { MutableInteractionSource() },
                onClick = { }
            )
            .padding(horizontal = 0.dp, vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = mainGreen,
                backgroundColor = Color.LightGray,
            )
        }
    }
}

@Composable
fun ContentWrapper(content: @Composable () -> Unit) {
    val isLoading by LoadingState.isLoading.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
        if (isLoading) {
            LoadingView()
        }
    }
}

