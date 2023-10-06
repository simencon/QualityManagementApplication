package com.simenko.qmapp.ui.main.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.main.setup.*
import com.simenko.qmapp.ui.main.main.content.*
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface MainPageState {
    val topScreenChannel: Channel<TopScreenIntent>
    fun trySendMainPageState(
        page: Page,
        onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick: ((MenuItem) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?,
        fabAction: (() -> Unit)?,
        refreshAction: (() -> Unit)?
    )
    fun trySendTabBadgesState(state: List<Triple<Int, Color, Color>>)
    fun trySendFabState(isVisible: Boolean)
    fun trySendEndOfListState(state: Boolean)
    fun trySendLoadingState(state: Pair<Boolean, String?>)
}

sealed class TopScreenIntent {
    data class MainPageSetup(
        val topBarSetup: TopBarSetup,
        val topTabsSetup: TopTabsSetup,
        val fabSetup: FabSetup,
        val pullRefreshSetup: PullRefreshSetup
    ) : TopScreenIntent()

    data class TabBadgesState(val state: List<Triple<Int, Color, Color>>) : TopScreenIntent()
    data class FabState(val state: Boolean) : TopScreenIntent()
    data class EndOfListState(val state: Boolean) : TopScreenIntent()
    data class LoadingState(val state: Pair<Boolean, String?>) : TopScreenIntent()
}

enum class Page(
    val navIcon: ImageVector?,
    val title: String?,
    val titlePlaceholderText: String?,
    val keyboardType: KeyboardType?,
    val searchBtnIcon: ImageVector?,
    val topTabsContent: List<TabItem>?,
    val fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?,
    val actionMenuItems: List<MenuItem>? = null
) {
    EMPTY_PAGE(null, null, null, null, null, null, null, null),

    INVESTIGATIONS(
        navIcon = Icons.Filled.Menu,
        title = "All investigations",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = InvestigationsActions.toList()
    ),
    PROCESS_CONTROL(
        navIcon = Icons.Filled.Menu,
        title = "Process control",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = ProcessControlActions.toList()
    ),
    TEAM(
        navIcon = Icons.Filled.Menu,
        title = "Company team",
        titlePlaceholderText = "Search by full name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = TeamTabs.toList(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = TeamActions.toList()
    ),

    ADD_EMPLOYEE(Icons.Filled.ArrowBack, "Add new employee", null, null, null, null, null, null),
    EDIT_EMPLOYEE(Icons.Filled.ArrowBack, "Edit employee", null, null, null, null, null, null),
    AUTHORIZE_USER(Icons.Filled.ArrowBack, "Authorize user", null, null, null, null, null, null),
    EDIT_USER(Icons.Filled.ArrowBack, "Edit user", null, null, null, null, null, null),
    NO_MODE(Icons.Filled.ArrowBack, "No mode", null, null, null, null, null, null),
    ADD_ORDER(Icons.Filled.ArrowBack, "New investigation order", null, null, null, null, null, null),
    EDIT_ORDER(Icons.Filled.ArrowBack, "Edit investigation order", null, null, null, null, null, null),
    ADD_SUB_ORDER(Icons.Filled.ArrowBack, "Add new sub order", null, null, null, null, null, null),
    EDIT_SUB_ORDER(Icons.Filled.ArrowBack, "Edit sub order", null, null, null, null, null, null),
    ADD_SUB_ORDER_SA(Icons.Filled.ArrowBack, "New process control order", null, null, null, null, null, null),
    EDIT_SUB_ORDER_SA(Icons.Filled.ArrowBack, "Edit process control order", null, null, null, null, null, null),
    ACCOUNT_EDIT(Icons.Filled.ArrowBack, "Edit account data", null, null, null, null, null, null),
    ;
}

interface MenuItem {
    val tag: String
    val title: String
    val image: ImageVector
    val group: MenuGroup

    enum class MenuGroup(val group: String) {
        COMPANY("Company"),
        QUALITY("Quality management"),
        GENERAL("General"),
        ACTIONS("Actions"),
        FILTER("Filter")
    }
}

interface TabItem {
    val index: Int
    val tag: SelectedNumber
    val title: String

    var badgeCount: Int
    var badgeBackgroundColor: Color
    var badgeContentColor: Color

    fun getBadge(): Triple<Int, Color, Color>
    fun updateBadge(state: Triple<Int, Color, Color>): TabItem

    interface Static {
        val startingTabItem: TabItem
    }
}