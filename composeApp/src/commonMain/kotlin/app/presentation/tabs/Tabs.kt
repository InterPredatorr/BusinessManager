package app.presentation.tabs

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BedroomParent
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.BedroomParent
import androidx.compose.material.icons.rounded.Bed
import androidx.compose.material.icons.rounded.BedroomParent
import androidx.compose.material.icons.rounded.Room
import androidx.compose.material.icons.rounded.RoomPreferences
import androidx.compose.material.icons.rounded.RoomService
import androidx.compose.material.icons.sharp.BedroomParent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import app.presentation.addworker.AddWorkerScreen
import app.presentation.rooms.RoomsScreen
import app.presentation.usersList.UsersListScreen
import app.presentation.worker.WorkerScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current==tab,
        onClick = { tabNavigator.current = tab },
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

object RoomsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Սենյակներ"
            val icon = rememberVectorPainter(Icons.Rounded.Bed)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = RoomsScreen())
    }
}

object AddWorkerTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Ավելացրեք աշխատող"
            val icon = rememberVectorPainter(Icons.Filled.Add)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = AddWorkerScreen())
    }
}

object WaitWorkTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Աշխատանք"
            val icon = rememberVectorPainter(Icons.Filled.Work)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = WorkerScreen())
    }
}

object WorkersTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Աշխատակազմ"
            val icon = rememberVectorPainter(Icons.Filled.Person)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = UsersListScreen())
    }
}

object ManageWorkersTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Աշխատակազմ"
            val icon = rememberVectorPainter(Icons.Default.Person)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = UsersListScreen())
    }
}