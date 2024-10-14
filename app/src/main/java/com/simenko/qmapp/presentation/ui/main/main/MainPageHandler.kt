package com.simenko.qmapp.presentation.ui.main.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.presentation.ui.main.main.content.MenuItem
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainPageHandler private constructor(
    private val mainPageState: MainPageState,
    private val page: Page,
    private val onNavMenuClick: (suspend (Boolean) -> Unit)?,
    private val onSearchAction: ((BaseFilter) -> Unit)?,
    private val onActionItemClick: ((MenuItem) -> Unit)?,
    private val onTabSelectAction: ((SelectedNumber) -> Unit)?,
    private val fabAction: (() -> Unit)?,
    private val refreshAction: (() -> Unit)?,
    private val tabBadges: Flow<List<Triple<Int, Color, Color>>>,

    private var onUpdateLoadingStateExtraAction: ((Triple<Boolean, Boolean, String?>) -> Unit)?
) {
    class Builder(private val page: Page, private val mainPageState: MainPageState) {
        private var onNavMenuClick: (suspend (Boolean) -> Unit)? = null
        private var onSearchAction: ((BaseFilter) -> Unit)? = null
        private var onActionItemClick: ((MenuItem) -> Unit)? = null
        private var onTabSelectAction: ((SelectedNumber) -> Unit)? = null
        private var fabAction: (() -> Unit)? = null
        private var refreshAction: (() -> Unit)? = null
        private var tabBadges: Flow<List<Triple<Int, Color, Color>>> = flow { }

        private var onUpdateLoadingStateExtraAction: ((Triple<Boolean, Boolean, String?>) -> Unit)? = null
        fun setOnNavMenuClickAction(action: (suspend (Boolean) -> Unit)?) = apply { this.onNavMenuClick = action }
        fun setOnSearchClickAction(action: ((BaseFilter) -> Unit)?) = apply { this.onSearchAction = action }
        fun setOnActionItemClickAction(action: ((MenuItem) -> Unit)?) = apply { this.onActionItemClick = action }
        fun setOnTabSelectAction(action: ((SelectedNumber) -> Unit)?) = apply { this.onTabSelectAction = action }
        fun setOnFabClickAction(action: (() -> Unit)?) = apply { this.fabAction = action }
        fun setOnPullRefreshAction(action: (() -> Unit)?) = apply { this.refreshAction = action }
        fun setTabBadgesFlow(flow: Flow<List<Triple<Int, Color, Color>>>) = apply { this.tabBadges = flow }

        fun setOnUpdateLoadingExtraAction(action: ((Triple<Boolean, Boolean, String?>) -> Unit)?) = apply { this.onUpdateLoadingStateExtraAction = action }

        fun build(): MainPageHandler {
            return MainPageHandler(
                mainPageState = mainPageState,
                page = page,
                onNavMenuClick = onNavMenuClick,
                onSearchAction = onSearchAction,
                onActionItemClick = onActionItemClick,
                onTabSelectAction = onTabSelectAction,
                fabAction = fabAction,
                refreshAction = refreshAction,
                tabBadges = tabBadges,
                onUpdateLoadingStateExtraAction = onUpdateLoadingStateExtraAction
            )
        }
    }

    val setupMainPage: suspend (Int, Boolean) -> Unit = { selectedTabIndex, isFabVisible ->
        mainPageState.sendMainPageState(page, onNavMenuClick, onSearchAction, onActionItemClick, onTabSelectAction, fabAction, refreshAction)
        mainPageState.sendSelectedTab(selectedTabIndex)
        mainPageState.sendFabVisibility(isFabVisible)
        tabBadges.collect { mainPageState.sendTabBadgesState(it) }
    }

    val setFabIcon: suspend (ImageVector) -> Unit = { mainPageState.sendFabIcon(it) }

    val updateLoadingState: (Triple<Boolean, Boolean, String?>) -> Unit = {
        onUpdateLoadingStateExtraAction?.invoke(it)
        mainPageState.trySendLoadingState(it)
    }
    val onListEnd: suspend (Boolean) -> Unit = { mainPageState.sendEndOfListState(it) }
    val setFabVisibilityState: (Boolean) -> Unit = {
        mainPageState.trySendFabVisibility(it)
    }
}













