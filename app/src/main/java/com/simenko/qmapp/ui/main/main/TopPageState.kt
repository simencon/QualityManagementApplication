package com.simenko.qmapp.ui.main.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Filter3
import androidx.compose.material.icons.filled.Filter4
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.FirstTabId
import com.simenko.qmapp.domain.FourthTabId
import com.simenko.qmapp.domain.SecondTabId
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.ThirdTabId
import com.simenko.qmapp.ui.main.main.components.FabSetup
import com.simenko.qmapp.ui.main.main.components.PullRefreshSetup
import com.simenko.qmapp.ui.main.main.components.TopBarSetup
import com.simenko.qmapp.ui.main.main.components.TopTabsSetup
import com.simenko.qmapp.utils.BaseFilter
import com.simenko.qmapp.utils.StringUtils.getWithSpaces
import kotlinx.coroutines.channels.Channel

interface TopPageState {
    val topScreenChannel: Channel<TopScreenIntent>
    fun trySendTopBarSetup(page: Page, onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick:((ActionItem) -> Unit)?)

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
    val actionMenuItems: List<ActionItem> = emptyList()
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

enum class ProgressTabs(val tabId: SelectedNumber) {
    ALL(FirstTabId),
    TO_DO(SecondTabId),
    IN_PROGRESS(ThirdTabId),
    DONE(FourthTabId);

    companion object {
        fun toListOfTriples() = ProgressTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

enum class TeamTabs(val tabId: SelectedNumber) {
    EMPLOYEES(FirstTabId),
    USERS(SecondTabId),
    REQUESTS(ThirdTabId);

    companion object {
        fun toListOfTriples() = TeamTabs.values().map { Triple(it.name, it.ordinal, it.tabId) }
    }
}

interface ActionItem {
    val tag: String
    val title: String
    val image: ImageVector
    val group: MenuItem.MenuGroup
}

enum class Common(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : ActionItem {
    UPLOAD_MASTER_DATA("upload_master_data", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    NO_FILTER("no_filter", Icons.Filled.FilterAltOff, MenuItem.MenuGroup.FILTER),
    CUSTOM_FILTER("custom_filter", Icons.Filled.FilterAlt, MenuItem.MenuGroup.FILTER)
    ;

    override val title: String get() = getWithSpaces(this.name)

    companion object {
        fun toList(): List<ActionItem> = Common.values().toList()
    }
}

enum class TeamActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : ActionItem {
    SYNC_INVESTIGATIONS("sync_investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    PPAP("ppap", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    INCOMING_INSPECTION("incoming_inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    PROCESS_CONTROL("process_control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    PRODUCT_AUDIT("product_audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER)
    ;

    override val title: String get() = getWithSpaces(this.name)

    companion object {
        fun toList(): List<ActionItem> = Common.toList().union(TeamActions.values().toList()).toList()
    }
}

enum class InvestigationsActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : ActionItem {
    SYNC_INVESTIGATIONS("sync_investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    PPAP("ppap", Icons.Filled.Filter1, MenuItem.MenuGroup.FILTER),
    INCOMING_INSPECTION("incoming_inspection", Icons.Filled.Filter2, MenuItem.MenuGroup.FILTER),
    PROCESS_CONTROL("process_control", Icons.Filled.Filter3, MenuItem.MenuGroup.FILTER),
    PRODUCT_AUDIT("product_audit", Icons.Filled.Filter4, MenuItem.MenuGroup.FILTER)
    ;

    override val title: String get() = getWithSpaces(this.name)

    companion object {
        fun toList(): List<ActionItem> = Common.toList().union(InvestigationsActions.values().toList()).toList()
    }
}

enum class ProcessControlActions(override val tag: String, override val image: ImageVector, override val group: MenuItem.MenuGroup) : ActionItem {
    SYNC_INVESTIGATIONS("sync_investigations", Icons.Filled.Refresh, MenuItem.MenuGroup.ACTIONS),
    ;

    override val title: String get() = getWithSpaces(this.name)

    companion object {
        fun toList(): List<ActionItem> = Common.toList().union(ProcessControlActions.values().toList()).toList()
    }
}