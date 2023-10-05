package com.simenko.qmapp.ui.main.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.ui.main.main.components.*
import com.simenko.qmapp.ui.main.main.content.*
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface MainPageState {
    val topScreenChannel: Channel<TopScreenIntent>
    fun trySendTopBarSetup(page: Page, onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick: ((MenuItem) -> Unit)?)

    fun trySendTopTabsSetup(page: Page, onTabSelectAction: ((SelectedNumber) -> Unit)?)
    fun trySendTabBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>)

    fun trySendTopScreenFabSetup(page: Page, fabAction: (() -> Unit)?)
    fun trySendEndOfListState(state: Boolean)

    fun trySendPullRefreshSetup(refreshAction: (() -> Unit)?)
    fun trySendLoadingState(state: Pair<Boolean, String?>)
}

sealed class TopScreenIntent {
    data class TopBarState(val titleSetup: TopBarSetup) : TopScreenIntent()
    data class TopTabsState(val topTabsSetup: TopTabsSetup) : TopScreenIntent()
    data class TabBadgeState(val tabIndex: Int, val state: Triple<Int, Color, Color>) : TopScreenIntent()
    data class TopScreenFabSetup(val fabSetup: FabSetup) : TopScreenIntent()
    data class EndOfListState(val state: Boolean) : TopScreenIntent()
    data class TopScreenPullRefreshSetup(val pullRefreshSetup: PullRefreshSetup) : TopScreenIntent()
    data class LoadingState(val state: Pair<Boolean, String?>) : TopScreenIntent()
}

enum class Page(
    val navIcon: ImageVector,
    val title: String,
    val titlePlaceholderText: String?,
    val keyboardType: KeyboardType?,
    val searchBtnIcon: ImageVector?,
    val topTabsContent: List<Triple<String, Int, SelectedNumber>>,
    val fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?,
    val actionMenuItems: List<MenuItem> = emptyList()
) {
    INVESTIGATIONS(
        navIcon = Icons.Filled.Menu,
        title = "All investigations",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toListOfTriples(),
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
        topTabsContent = ProgressTabs.toListOfTriples(),
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
        topTabsContent = TeamTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert,
        actionMenuItems = TeamActions.toList()
    ),


    ADD_EMPLOYEE(Icons.Filled.ArrowBack, "Add new employee", null, null, null, emptyList(), null, null),
    EDIT_EMPLOYEE(Icons.Filled.ArrowBack, "Edit employee", null, null, null, emptyList(), null, null),
    AUTHORIZE_USER(Icons.Filled.ArrowBack, "Authorize user", null, null, null, emptyList(), null, null),
    EDIT_USER(Icons.Filled.ArrowBack, "Edit user", null, null, null, emptyList(), null, null),
    NO_MODE(Icons.Filled.ArrowBack, "No mode", null, null, null, emptyList(), null, null),
    ADD_ORDER(Icons.Filled.ArrowBack, "New investigation order", null, null, null, emptyList(), null, null),
    EDIT_ORDER(Icons.Filled.ArrowBack, "Edit investigation order", null, null, null, emptyList(), null, null),
    ADD_SUB_ORDER(Icons.Filled.ArrowBack, "Add new sub order", null, null, null, emptyList(), null, null),
    EDIT_SUB_ORDER(Icons.Filled.ArrowBack, "Edit sub order", null, null, null, emptyList(), null, null),
    ADD_SUB_ORDER_SA(Icons.Filled.ArrowBack, "New process control order", null, null, null, emptyList(), null, null),
    EDIT_SUB_ORDER_SA(Icons.Filled.ArrowBack, "Edit process control order", null, null, null, emptyList(), null, null),
    ACCOUNT_EDIT(Icons.Filled.ArrowBack, "Edit account data", null, null, null, emptyList(), null, null),
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