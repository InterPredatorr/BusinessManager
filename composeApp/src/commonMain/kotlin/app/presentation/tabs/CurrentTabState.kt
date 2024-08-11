package app.presentation.tabs

import cafe.adriel.voyager.navigator.tab.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CurrentTabState {
    private val _current = MutableStateFlow<Tab>(RoomsTab)
    val current: StateFlow<Tab> = _current.asStateFlow()

    fun setTab(tab: Tab) {
        _current.value = tab
    }
}


