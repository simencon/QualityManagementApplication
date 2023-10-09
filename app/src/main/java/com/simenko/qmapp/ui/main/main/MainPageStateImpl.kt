package com.simenko.qmapp.ui.main.main

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.setup.*
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class MainPageStateImpl @Inject constructor() : MainPageState {
    override val topScreenChannel: Channel<TopScreenIntent> = Channel(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override suspend fun sendMainPageState(
        page: Page,
        onSearchAction: ((BaseFilter) -> Unit)?,
        onActionItemClick: ((MenuItem) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?,
        fabAction: (() -> Unit)?,
        refreshAction: (() -> Unit)?
    ) {
        topScreenChannel.send(
            TopScreenIntent.MainPageSetup(
                topBarSetup = TopBarSetup(page, onSearchAction, onActionItemClick),
                topTabsSetup = TopTabsSetup(page, onTabSelectAction),
                fabSetup = FabSetup(page, fabAction),
                pullRefreshSetup = PullRefreshSetup(refreshAction)
            )
        )
    }

    override fun trySendMainPageState(
        page: Page,
        onSearchAction: ((BaseFilter) -> Unit)?,
        onActionItemClick: ((MenuItem) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?,
        fabAction: (() -> Unit)?,
        refreshAction: (() -> Unit)?
    ) {
        topScreenChannel.trySend(
            TopScreenIntent.MainPageSetup(
                topBarSetup = TopBarSetup(page, onSearchAction, onActionItemClick),
                topTabsSetup = TopTabsSetup(page, onTabSelectAction),
                fabSetup = FabSetup(page, fabAction),
                pullRefreshSetup = PullRefreshSetup(refreshAction)
            )
        )
    }

    override suspend fun sendTabBadgesState(state: List<Triple<Int, Color, Color>>) {
        topScreenChannel.send(TopScreenIntent.TabBadgesState(state))
    }

    override fun trySendTabBadgesState(state: List<Triple<Int, Color, Color>>) {
        topScreenChannel.trySend(TopScreenIntent.TabBadgesState(state))
    }

    override suspend fun sendSelectedTab(selectedTab: Int) {
        topScreenChannel.send(TopScreenIntent.SelectedTabState(state = selectedTab))
    }

    override fun trySendSelectedTab(selectedTab: Int) {
        topScreenChannel.trySend(TopScreenIntent.SelectedTabState(state = selectedTab))
    }

    override suspend fun sendFabVisibility(isVisible: Boolean) {
        topScreenChannel.send(TopScreenIntent.FabVisibilityState(state = isVisible))
    }

    override fun trySendFabVisibility(isVisible: Boolean) {
        topScreenChannel.trySend(TopScreenIntent.FabVisibilityState(state = isVisible))
    }

    override suspend fun sendEndOfListState(state: Boolean) {
        topScreenChannel.send(TopScreenIntent.EndOfListState(state))
    }

    override fun trySendEndOfListState(state: Boolean) {
        topScreenChannel.trySend(TopScreenIntent.EndOfListState(state))
    }

    override suspend fun sendLoadingState(state: Pair<Boolean, String?>) {
        topScreenChannel.send(TopScreenIntent.LoadingState(state))
    }

    override fun trySendLoadingState(state: Pair<Boolean, String?>) {
        topScreenChannel.trySend(TopScreenIntent.LoadingState(state))
    }
}