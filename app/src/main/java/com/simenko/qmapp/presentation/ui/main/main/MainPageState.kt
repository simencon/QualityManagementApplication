package com.simenko.qmapp.presentation.ui.main.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.presentation.ui.main.main.content.MenuItem
import com.simenko.qmapp.presentation.ui.main.main.setup.FabSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.PullRefreshSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.TopBarSetup
import com.simenko.qmapp.presentation.ui.main.main.setup.TopTabsSetup
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel
import com.simenko.qmapp.presentation.ui.main.main.content.Page

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
    suspend fun sendFabIcon(icon: ImageVector)
    fun trySendFabIcon(icon: ImageVector)
    suspend fun sendEndOfListState(state: Boolean)
    fun trySendEndOfListState(state: Boolean)
    suspend fun sendLoadingState(state: Triple<Boolean, Boolean, String?>)
    fun trySendLoadingState(state: Triple<Boolean, Boolean, String?>)
}

sealed interface TopScreenIntent {
    data class MainPageSetup(
        val topBarSetup: TopBarSetup,
        val topTabsSetup: TopTabsSetup,
        val fabSetup: FabSetup,
        val pullRefreshSetup: PullRefreshSetup
    ) : TopScreenIntent

    data class TabBadgesState(val state: List<Triple<Int, Color, Color>>) : TopScreenIntent
    class SelectedTabState(val state: Int) : TopScreenIntent
    data class FabVisibilityState(val state: Boolean) : TopScreenIntent
    data class FabIconState(val state: ImageVector) : TopScreenIntent
    data class EndOfListState(val state: Boolean) : TopScreenIntent
    data class LoadingState(val state: Triple<Boolean, Boolean, String?>) : TopScreenIntent
}