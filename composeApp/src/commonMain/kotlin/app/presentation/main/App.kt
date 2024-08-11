package app.presentation.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import app.data.user.GetUserFirebaseApi
import app.domain.user.UserType
import app.presentation.components.Loading.ContentWrapper
import app.presentation.login.LoginScreen
import app.presentation.login.LoginViewModel
import app.presentation.onApplicationStartPlatformSpecific
import app.presentation.splash.NextAction
import app.presentation.splash.splashView
import app.presentation.tabs.AddWorkerTab
import app.presentation.tabs.CurrentTabState
import app.presentation.tabs.ManageWorkersTab
import app.presentation.tabs.RoomsTab
import app.presentation.tabs.WaitWorkTab
import app.presentation.tabs.WorkersTab
import app.presentation.theme.AppTheme
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App() {
    val userApi = GetUserFirebaseApi()

    val nextAction: MutableState<NextAction> = remember { mutableStateOf(NextAction.Splash) }
    var myPushNotificationToken by remember { mutableStateOf("") }


    LaunchedEffect(true) {

        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                myPushNotificationToken = token
                println("onNewToken: $token")
                CoroutineScope(Dispatchers.IO).launch {
                    userApi.updateCurrentUserData(mapOf("deviceToken" to token))
                }

            }
        })
        myPushNotificationToken = NotifierManager.getPushNotifier().getToken() ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            userApi.updateCurrentUserData(mapOf("deviceToken" to myPushNotificationToken))
        }
        println("onNewToken: $myPushNotificationToken")
    }
    ContentWrapper {
        when (val action = nextAction.value) {
            is NextAction.LoginAction -> LoginScreen(nextAction)
            is NextAction.MainScreen -> {
                mainScreen(action.userType) {
                    nextAction.value = NextAction.LoginAction
                }
            }

            is NextAction.Splash -> splashView(nextAction)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun mainScreen(userType: UserType, onLogOut: () -> Unit) {

    val viewModel: LoginViewModel = koinInject()
    val currentTab by CurrentTabState.current.collectAsState()
    var olineToggleChecked by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        val user = viewModel.getCurrentUser()
        olineToggleChecked = user?.active ?: false
        userName = user?.name ?: ""
    }

    AppTheme {
        TabNavigator(currentTab) {
            BottomSheetNavigator(
                sheetGesturesEnabled = false,
                modifier = Modifier.animateContentSize(),
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                skipHalfExpanded = true
            ) {
                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        TopAppBar(
                            backgroundColor = Color.White,
                            title = {
                                Row {
                                    Text(
                                        "Իսրայել Օրի",
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        userName,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = Dp(20f))
                                    )
                                }
                            },
                            actions = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Transparent,
                                        contentColor = mainGreen
                                    ),
                                    onClick = {
                                        viewModel.logOut(completion = onLogOut)
                                    },
                                    elevation = ButtonDefaults.elevation(0.dp)
                                ) {
                                    Text(
                                        text = "Ելք",
                                        fontSize = TextUnit(
                                            16F,
                                            TextUnitType.Sp
                                        )
                                    )
                                }
                                Switch(
                                    colors = SwitchDefaults.colors(checkedThumbColor = mainGreen),
                                    checked = olineToggleChecked, onCheckedChange = {
                                        olineToggleChecked = it
                                        if (olineToggleChecked) {
                                            viewModel.makeUserOnline()
                                        } else {
                                            viewModel.makeUserOffline()
                                        }
                                    })
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigation(
                            contentColor = Color.Gray,
                            backgroundColor = Color.White,
                        ) {
                            when (userType) {
                                UserType.Worker -> {
                                    TabNavigationItem(WaitWorkTab)
                                }

                                UserType.Manager -> {
                                    TabNavigationItem(RoomsTab)
                                    TabNavigationItem(WorkersTab)
                                    TabNavigationItem(AddWorkerTab)
                                }

                                UserType.Owner -> {
                                    TabNavigationItem(RoomsTab)
                                    TabNavigationItem(ManageWorkersTab)
                                    TabNavigationItem(AddWorkerTab)
                                }
                            }
                        }
                    },

                    ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding),
                    ) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current

    LaunchedEffect(true) {
        tabNavigator.current = CurrentTabState.current.value
    }

    BottomNavigationItem(
        unselectedContentColor = Color.LightGray,
        selectedContentColor = mainGreen,
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription =
                    tab.options.title
                )
            }
        }
    )
}

object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("Push Notification onNewToken: $token")
            }

            override fun onPayloadData(data: PayloadData) {
                super.onPayloadData(data)
                println("Push Notification payloadData: $data")
            }
        })
    }
}

