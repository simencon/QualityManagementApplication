package com.simenko.qmapp.ui.main.main.page.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.page.MainPage
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopBarSetup(
    private val mainPage: MainPage = MainPage.values()[0],
    var onSearchAction: ((BaseFilter) -> Unit)? = null
) {
    val navIcon = mainPage.navIcon
    val title: String = mainPage.title
    val placeholderText: String? = mainPage.titlePlaceholderText
    val keyboardType: KeyboardType? = mainPage.keyboardType
    val titleBtnIcon: ImageVector? = mainPage.searchBtnIcon
    val actionBtnIcon: ImageVector? = mainPage.actionBtnIcon

    private val _selectedDrawerMenuItemId: MutableStateFlow<String> = MutableStateFlow(MenuItem.getStartingDrawerMenuItem().id)
    val selectedDrawerMenuItemId: StateFlow<String> get() = _selectedDrawerMenuItemId
    fun setDrawerMenuItemId(id: String) {
        this._selectedDrawerMenuItemId.value = id
    }

    private val _drawerMenuState = MutableStateFlow(DrawerState(DrawerValue.Closed))
    val drawerMenuState = _drawerMenuState.asStateFlow()
    suspend fun setDrawerMenuState(value: Boolean) {
        if (value) _drawerMenuState.value.open() else _drawerMenuState.value.close()
    }

    private val _searchBarState = MutableStateFlow(false)
    val searchBarState = _searchBarState.asStateFlow()
    fun setSearchBarState(value: Boolean) {
        _searchBarState.value = value
    }

    private val _actionsMenuState = MutableStateFlow(false)
    val actionsMenuState = _actionsMenuState.asStateFlow()
    fun setActionMenuState(value: Boolean) {
        _actionsMenuState.value = value
    }
}