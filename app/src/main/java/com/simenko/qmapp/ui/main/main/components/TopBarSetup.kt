package com.simenko.qmapp.ui.main.main.components

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.ui.main.main.ActionItem
import com.simenko.qmapp.ui.main.main.Common
import com.simenko.qmapp.ui.main.main.InvestigationsActions
import com.simenko.qmapp.ui.main.main.MenuItem
import com.simenko.qmapp.ui.main.main.Page
import com.simenko.qmapp.ui.main.main.ProcessControlActions
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TopBarSetup(
    private val page: Page = Page.values()[0],
    var onSearchAction: ((BaseFilter) -> Unit)? = null,
    var onActionItemClick:((ActionItem) -> Unit)? = null
) {
    val navIcon = page.navIcon
    val title: String = page.title
    val placeholderText: String? = page.titlePlaceholderText
    val keyboardType: KeyboardType? = page.keyboardType
    val titleBtnIcon: ImageVector? = page.searchBtnIcon
    val actionBtnIcon: ImageVector? = page.actionBtnIcon
    val actionMenuItems: List<ActionItem> = page.actionMenuItems

    private val _selectedDrawerMenuItemId = MutableStateFlow(MenuItem.getStartingDrawerMenuItem().id)
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

    private val _selectedActionMenuItem = MutableStateFlow(Common.NO_FILTER as ActionItem)
    val selectedActionMenuItem = _selectedActionMenuItem.asStateFlow()

    fun onActionMenuItemClick(item: ActionItem) {
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

    private fun onSelectableActionMenuItemClick(item: ActionItem) {
        _selectedActionMenuItem.value = item
    }

    private fun onNotSelectableActionMenuItemClick(item: ActionItem) {
    }
}