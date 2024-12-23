package com.simenko.qmapp.presentation.ui.main.main.setup

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.presentation.ui.main.main.content.Common
import com.simenko.qmapp.presentation.ui.main.main.content.InvestigationsActions
import com.simenko.qmapp.presentation.ui.main.main.content.MenuItem
import com.simenko.qmapp.presentation.ui.main.main.content.ProcessControlActions
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopBarSetup(
    private val page: Page = Page.values()[0],
    var onNavMenuClick: (suspend (Boolean) -> Unit)? = null,
    var onSearchAction: ((BaseFilter) -> Unit)? = null,
    var onActionItemClick: ((MenuItem) -> Unit)? = null
) {
    init {
        if (onNavMenuClick == null) onNavMenuClick = { this.setDrawerMenuState(it) }
    }

    val link: String = page.drawerLink
    val navIcon: ImageVector? = page.navIcon
    val title: String? = page.title
    val placeholderText: String? = page.titlePlaceholderText
    val keyboardType: KeyboardType? = page.keyboardType
    val titleBtnIcon: ImageVector? = page.searchBtnIcon
    val actionBtnIcon: ImageVector? = page.actionBtnIcon
    val actionMenuItems: List<MenuItem>? = page.actionMenuItems

    private val _drawerMenuState = MutableStateFlow(DrawerState(DrawerValue.Closed))
    val drawerMenuState = _drawerMenuState.asStateFlow()
    private suspend fun setDrawerMenuState(value: Boolean) {
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

    private val _selectedActionMenuItem = MutableStateFlow(Common.NO_FILTER as MenuItem)
    val selectedActionMenuItem = _selectedActionMenuItem.asStateFlow()

    fun onActionMenuItemClick(item: MenuItem) {
        when (item) {
            Common.CUSTOM_FILTER, Common.UPLOAD_MASTER_DATA, InvestigationsActions.SYNC_INVESTIGATIONS, ProcessControlActions.SYNC_INVESTIGATIONS -> {
                onNotSelectableActionMenuItemClick(item)
            }

            else -> {
                onSelectableActionMenuItemClick(item)
            }
        }
        onActionItemClick?.invoke(item)
    }

    private fun onSelectableActionMenuItemClick(item: MenuItem) {
        _selectedActionMenuItem.value = item
    }

    private fun onNotSelectableActionMenuItemClick(item: MenuItem) {
    }
}