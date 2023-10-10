package com.simenko.qmapp.ui.main.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.main.setup.*
import com.simenko.qmapp.ui.main.main.content.*
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface MainPageState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendMainPageState(
        page: Page,
        onNavMenuClick: (suspend (Boolean) -> Unit)?,
        onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick: ((MenuItem) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?,
        fabAction: (() -> Unit)?,
        refreshAction: (() -> Unit)?
    )

    fun trySendMainPageState(
        page: Page,
        onNavMenuClick: (suspend (Boolean) -> Unit)?,
        onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick: ((MenuItem) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?,
        fabAction: (() -> Unit)?,
        refreshAction: (() -> Unit)?
    )

    suspend fun sendTabBadgesState(state: List<Triple<Int, Color, Color>>)
    fun trySendTabBadgesState(state: List<Triple<Int, Color, Color>>)
    suspend fun sendSelectedTab(selectedTab: Int)
    fun trySendSelectedTab(selectedTab: Int)
    suspend fun sendFabVisibility(isVisible: Boolean)
    fun trySendFabVisibility(isVisible: Boolean)
    suspend fun sendEndOfListState(state: Boolean)
    fun trySendEndOfListState(state: Boolean)
    suspend fun sendLoadingState(state: Pair<Boolean, String?>)
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
    class SelectedTabState(val state: Int) : TopScreenIntent()
    data class FabVisibilityState(val state: Boolean) : TopScreenIntent()
    data class EndOfListState(val state: Boolean) : TopScreenIntent()
    data class LoadingState(val state: Pair<Boolean, String?>) : TopScreenIntent()
}

enum class Page(
    val link: String,
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
    EMPTY_PAGE(EmptyString.str, null, null, null, null, null, null, null, null, null),

    TEAM(
        link = Route.Main.Team.link,
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

    INVESTIGATIONS(
        link = Route.Main.Inv.link,
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
        link = Route.Main.ProcessControl.link,
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
    ACCOUNT_SETTINGS(
        link = Route.Main.Settings.link,
        navIcon = Icons.Filled.Menu,
        title = "Account settings",
        titlePlaceholderText = null,
        keyboardType = null,
        searchBtnIcon = null,
        topTabsContent = null,
        fabIcon = null,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = AccountSettingsActions.toList()
    ),

    ADD_EMPLOYEE(Route.Main.Team.EmployeeAddEdit.link, Icons.Filled.ArrowBack, "Add new employee", null, null, null, null, null, null),
    EDIT_EMPLOYEE(Route.Main.Team.EmployeeAddEdit.link, Icons.Filled.ArrowBack, "Edit employee", null, null, null, null, null, null),
    AUTHORIZE_USER(Route.Main.Team.AuthorizeUser.link, Icons.Filled.ArrowBack, "Authorize user", null, null, null, null, null, null),
    EDIT_USER(Route.Main.Team.EditUser.link, Icons.Filled.ArrowBack, "Edit user", null, null, null, null, null, null),
    ADD_ORDER(Route.Main.OrderAddEdit.link, Icons.Filled.ArrowBack, "New investigation order", null, null, null, null, null, null),
    EDIT_ORDER(Route.Main.OrderAddEdit.link, Icons.Filled.ArrowBack, "Edit investigation order", null, null, null, null, null, null),
    ADD_SUB_ORDER(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Add new sub order", null, null, null, null, null, null),
    EDIT_SUB_ORDER(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Edit sub order", null, null, null, null, null, null),
    ADD_SUB_ORDER_SA(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "New process control order", null, null, null, null, null, null),
    EDIT_SUB_ORDER_SA(Route.Main.SubOrderAddEdit.link, Icons.Filled.ArrowBack, "Edit process control order", null, null, null, null, null, null),

    ACCOUNT_EDIT(Route.Main.Settings.EditUserDetails.link, Icons.Filled.ArrowBack, "Edit account data", null, null, null, null, Icons.Filled.Save, null),
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