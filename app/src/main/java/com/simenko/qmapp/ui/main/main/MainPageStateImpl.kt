package com.simenko.qmapp.ui.main.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.setup.*
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
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

@Composable
fun StateChangedEffect(
    topScreenChannel: Channel<TopScreenIntent>,
    onStartHappen: Boolean,
    onMainPageSetupIntent: (TopBarSetup, TopTabsSetup, FabSetup, PullRefreshSetup) -> Unit = { _, _, _, _ -> },

    onTopBadgeStatesIntent: (List<Triple<Int, Color, Color>>) -> Unit = {},
    onSelectedTabStateIntent: (Int) -> Unit = {},
    onFabVisibilityStateIntent: (Boolean) -> Unit = {},
    onEndOfListStateIntent: (Boolean) -> Unit = {},
    onLoadingStateIntent: (Pair<Boolean, String?>) -> Unit = {},
) {
    LaunchedEffect(topScreenChannel, onStartHappen) {
        Log.d("TopScreenStateImpl", "lifecycleState: $onStartHappen")
        if (onStartHappen)
            topScreenChannel.receiveAsFlow().collect { intent ->
                Log.d("TopScreenStateImpl", "StateChangedEffect: $intent")
                when (intent) {
                    is TopScreenIntent.MainPageSetup -> onMainPageSetupIntent(intent.topBarSetup, intent.topTabsSetup, intent.fabSetup, intent.pullRefreshSetup)
                    is TopScreenIntent.TabBadgesState -> onTopBadgeStatesIntent(intent.state)
                    is TopScreenIntent.SelectedTabState -> onSelectedTabStateIntent(intent.state)
                    is TopScreenIntent.FabVisibilityState -> onFabVisibilityStateIntent(intent.state)
                    is TopScreenIntent.EndOfListState -> onEndOfListStateIntent(intent.state)
                    is TopScreenIntent.LoadingState -> onLoadingStateIntent(intent.state)
                }
            }
    }
}