package com.simenko.qmapp.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.ProgressTabs
import com.simenko.qmapp.ui.main.main.TeamTabs
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>)
    fun trySendLoadingState(loadingState: Pair<Boolean, String?>)

    fun trySendEndOfListState(state: Boolean)

    fun trySendTopBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>)

    fun trySendTopScreenSetup(
        addEditMode: Pair<AddEditMode, () -> Unit>,
        refreshAction: () -> Unit,
        filterAction: (BaseFilter) -> Unit
    )

    fun trySendTopScreenSetupDev(
        mainPage: MainPage,

        onNavBtnClick: suspend (Boolean) -> Unit,
        onSearchBtnClick: (Boolean) -> Unit,
        onActionBtnClick: (Boolean) -> Unit,

        onTabClickAction: (Int) -> Unit,

        fabAction: () -> Unit,

        refreshAction: () -> Unit,
        filterAction: (BaseFilter) -> Unit
    )
}

sealed class TopScreenIntent {
    data class LoadingState(
        val loadingState: Pair<Boolean, String?> = Pair(false, null)
    ) : TopScreenIntent()

    data class EndOfListState(
        val state: Boolean
    ) : TopScreenIntent()

    data class TopBadgeState(
        val tabIndex: Int,
        val state: Triple<Int, Color, Color>
    ) : TopScreenIntent()

    data class TopScreenSetup(
        val addEditMode: AddEditMode,
        val addEditAction: () -> Unit,
        val refreshAction: () -> Unit,
        val filterAction: (BaseFilter) -> Unit
    ) : TopScreenIntent()

    data class TopScreenSetupDev(
        val titleSetup: TopBarSetup,
        val topTabsSetup: TopTabsContent,
        val fabSetup: FabSetup,
        val refreshAction: () -> Unit,
        val filterAction: (BaseFilter) -> Unit
    ) : TopScreenIntent()
}

data class TopBarSetup(
    private val mainPage: MainPage = MainPage.values()[0],
    var onNavBtnClick: suspend (Boolean) -> Unit = {},
    var onSearchBtnClick: (Boolean) -> Unit = {},
    var onActionBtnClick: (Boolean) -> Unit = {}
) {
    val navIcon = mainPage.navIcon
    val title: String = mainPage.title
    val placeholderText: String? = mainPage.titlePlaceholderText
    val titleBtnIcon: ImageVector? = mainPage.titleBtnIcon
    val actionBtnIcon: ImageVector? = mainPage.actionBtnIcon
}

data class TopTabsContent(
    private val screen: MainPage = MainPage.values()[0],
    var onTabClickAction: (Int) -> Unit = {}
) {
    val topTabsContent: List<Triple<String, Int, SelectedNumber>>? = screen.topTabsContent
}

data class FabSetup(
    private val screen: MainPage,
    val fabAction: () -> Unit
) {
    val fabIcon: ImageVector? = screen.fabIcon
}

enum class MainPage(
    val navIcon: ImageVector,
    val title: String,
    val titlePlaceholderText: String?,
    val titleBtnIcon: ImageVector?,
    val topTabsContent: List<Triple<String, Int, SelectedNumber>>?,
    val fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?
) {
    INVESTIGATIONS(
        navIcon = Icons.Filled.Menu,
        title = "All investigations",
        titlePlaceholderText = "Search order by number",
        titleBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
    ),
    PROCESS_CONTROL(
        navIcon = Icons.Filled.Menu,
        title = "Process control",
        titlePlaceholderText = "Search order by number",
        titleBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
    ),

    TEAM(
        navIcon = Icons.Filled.Menu,
        title = "Company team",
        titlePlaceholderText = "Search by full name",
        titleBtnIcon = Icons.Filled.Search,
        topTabsContent = TeamTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
    ),


    ADD_EMPLOYEE(Icons.Filled.ArrowBack, "Add new employee", null, null, null, null, null),
    EDIT_EMPLOYEE(Icons.Filled.ArrowBack, "Edit employee", null, null, null, null, null),
    AUTHORIZE_USER(Icons.Filled.ArrowBack, "Authorize user", null, null, null, null, null),
    EDIT_USER(Icons.Filled.ArrowBack, "Edit user", null, null, null, null, null),
    NO_MODE(Icons.Filled.ArrowBack, "No mode", null, null, null, null, null),
    ADD_ORDER(Icons.Filled.ArrowBack, "New investigation order", null, null, null, null, null),
    EDIT_ORDER(Icons.Filled.ArrowBack, "Edit investigation order", null, null, null, null, null),
    ADD_SUB_ORDER(Icons.Filled.ArrowBack, "Add new sub order", null, null, null, null, null),
    EDIT_SUB_ORDER(Icons.Filled.ArrowBack, "Edit sub order", null, null, null, null, null),
    ADD_SUB_ORDER_SA(Icons.Filled.ArrowBack, "New process control order", null, null, null, null, null),
    EDIT_SUB_ORDER_SA(Icons.Filled.ArrowBack, "Edit process control order", null, null, null, null, null),
    ACCOUNT_EDIT(Icons.Filled.ArrowBack, "Edit account data", null, null, null, null, null),
    ;
}