package com.simenko.qmapp.ui.main.main.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.ProgressTabs
import com.simenko.qmapp.ui.main.main.TeamTabs
import com.simenko.qmapp.ui.main.main.page.components.FabSetup
import com.simenko.qmapp.ui.main.main.page.components.TopTabsSetup
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>)
    fun trySendLoadingState(loadingState: Pair<Boolean, String?>)

    fun trySendEndOfListState(state: Boolean)

    fun trySendTopBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>)

    fun trySendTopScreenSetupDev(
        mainPage: MainPage,

        onNavBtnClick: (suspend (Boolean) -> Unit)?,
        onSearchBtnClick: ((Boolean) -> Unit)?,
        onSearchAction: ((BaseFilter) -> Unit)?,
        onActionBtnClick: ((Boolean) -> Unit)?,

        onTabSelectAction: ((SelectedNumber) -> Unit)?,

        refreshAction: () -> Unit
    )

    fun trySendTopScreenFabSetup(
        mainPage: MainPage,
        fabAction: (() -> Unit)?,
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

    data class TopScreenSetupDev(
        val titleSetup: TopBarSetup,
        val topTabsSetup: TopTabsSetup,
        val refreshAction: () -> Unit,
    ) : TopScreenIntent()

    data class TopScreenFabSetup(
        val fabSetup: FabSetup
    ) : TopScreenIntent()
}

data class TopBarSetup(
    private val mainPage: MainPage = MainPage.values()[0],
    var onNavBtnClick: (suspend (Boolean) -> Unit)? = null,
    var onSearchBtnClick: ((Boolean) -> Unit)? = null,
    var onSearchAction: ((BaseFilter) -> Unit)? = null,
    var onActionBtnClick: ((Boolean) -> Unit)? = null,
) {
    val navIcon = mainPage.navIcon
    val title: String = mainPage.title
    val placeholderText: String? = mainPage.titlePlaceholderText
    val keyboardType: KeyboardType? = mainPage.keyboardType
    val titleBtnIcon: ImageVector? = mainPage.searchBtnIcon
    val actionBtnIcon: ImageVector? = mainPage.actionBtnIcon
}

enum class MainPage(
    val navIcon: ImageVector,
    val title: String,
    val titlePlaceholderText: String?,
    val keyboardType: KeyboardType?,
    val searchBtnIcon: ImageVector?,
    val topTabsContent: List<Triple<String, Int, SelectedNumber>>,
    val fabIcon: ImageVector?,
    val actionBtnIcon: ImageVector?
) {
    INVESTIGATIONS(
        navIcon = Icons.Filled.Menu,
        title = "All investigations",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
    ),
    PROCESS_CONTROL(
        navIcon = Icons.Filled.Menu,
        title = "Process control",
        titlePlaceholderText = "Search order by number",
        keyboardType = KeyboardType.Decimal,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = ProgressTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
    ),

    TEAM(
        navIcon = Icons.Filled.Menu,
        title = "Company team",
        titlePlaceholderText = "Search by full name",
        keyboardType = KeyboardType.Text,
        searchBtnIcon = Icons.Filled.Search,
        topTabsContent = TeamTabs.toListOfTriples(),
        fabIcon = Icons.Filled.Add,
        actionBtnIcon = Icons.Filled.MoreVert
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