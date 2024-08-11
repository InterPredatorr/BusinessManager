package app.presentation.usersList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import app.presentation.theme.mainGreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import org.koin.compose.koinInject

class UsersListScreen: Screen {

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: UsersListViewModel = koinInject()

        val ownerTabs = listOf("Աշխատողներ", "Մենեջերներ")
        val managerTabs = listOf("Աշխատողներ")

        LaunchedEffect(true) {
            viewModel.initData()
        }

        fun tabs(): List<String> = (if (false) ownerTabs else managerTabs)

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            TabRow(
                selectedTabIndex = viewModel.selectedUserTab.value.raw
            ) {
                tabs().forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier
                            .background(mainGreen),
                        text = { Text(title, fontWeight = FontWeight.Bold) },
                        selected = viewModel.selectedUserTab.value.raw == index,
                        onClick = {
                            SelectedUserTab.entries.firstOrNull { it.raw == index }?.let { selectedTab ->
                                viewModel.selectedUserTab.value = selectedTab
                                viewModel.setTabUsers()
                            }
                        }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(viewModel.currentTabUsers.value) {
                    UserCardView(it) {
                        bottomSheetNavigator.hide()
                        viewModel.getUsersList()
                    }
                }
            }
        }
    }

}
