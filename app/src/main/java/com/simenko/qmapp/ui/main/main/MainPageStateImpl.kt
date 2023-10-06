package com.simenko.qmapp.ui.main.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.setup.*
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class MainPageStateImpl @Inject constructor() : MainPageState {
    override val topScreenChannel: Channel<TopScreenIntent> = Channel(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override fun trySendTopBarSetup(page: Page, onSearchAction: ((BaseFilter) -> Unit)?, onActionItemClick:((MenuItem) -> Unit)?) {
        topScreenChannel.trySend(
            TopScreenIntent.TopBarState(
                titleSetup = TopBarSetup(page, onSearchAction, onActionItemClick),
            )
        )
    }

    override fun trySendTopTabsSetup(page: Page, onTabSelectAction: ((SelectedNumber) -> Unit)?) {
        topScreenChannel.trySend(
            TopScreenIntent.TopTabsState(
                topTabsSetup = TopTabsSetup(page, onTabSelectAction)
            )
        )
    }

    override fun trySendTabBadgesState(state: List<Triple<Int, Color, Color>>) {
        topScreenChannel.trySend(
            TopScreenIntent.TabBadgesState(state)
        )
    }

    override fun trySendTopScreenFabSetup(page: Page, fabAction: (() -> Unit)?) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenFabSetup(
                fabSetup = FabSetup(page, fabAction)
            )
        )
    }

    override fun trySendEndOfListState(state: Boolean) {
        topScreenChannel.trySend(
            TopScreenIntent.EndOfListState(state)
        )
    }

    override fun trySendPullRefreshSetup(refreshAction: (() -> Unit)?) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenPullRefreshSetup(
                pullRefreshSetup = PullRefreshSetup(refreshAction)
            )
        )
    }

    override fun trySendLoadingState(state: Pair<Boolean, String?>) {
        topScreenChannel.trySend(
            TopScreenIntent.LoadingState(state)
        )
    }
}

@Composable
fun StateChangedEffect(
    topScreenChannel: Channel<TopScreenIntent>,

    onTopBarSetupIntent: (TopBarSetup) -> Unit = {},

    onTopTabsSetupIntent: (TopTabsSetup) -> Unit = {},
    onTopBadgeStatesIntent: (List<Triple<Int, Color, Color>>) -> Unit = {},

    onTopScreenFabSetupIntent: (FabSetup) -> Unit = {},
    onEndOfListIntent: (Boolean) -> Unit = {},

    onTopScreenPullRefreshSetupIntent: (PullRefreshSetup) -> Unit = {},
    onLoadingState: (Pair<Boolean, String?>) -> Unit = {}
) {
    LaunchedEffect(topScreenChannel) {
        topScreenChannel.receiveAsFlow().collect { intent ->
            Log.d("TopScreenStateImpl", "StateChangedEffect: $intent")
            when (intent) {
                is TopScreenIntent.TopBarState -> {
                    onTopBarSetupIntent(intent.titleSetup)
                }

                is TopScreenIntent.TopTabsState -> {
                    onTopTabsSetupIntent(intent.topTabsSetup)
                }

                is TopScreenIntent.TabBadgesState -> {
                    onTopBadgeStatesIntent(intent.state)
                }

                is TopScreenIntent.TopScreenFabSetup -> {
                    onTopScreenFabSetupIntent(intent.fabSetup)
                }

                is TopScreenIntent.EndOfListState -> {
                    onEndOfListIntent(intent.state)
                }

                is TopScreenIntent.TopScreenPullRefreshSetup -> {
                    onTopScreenPullRefreshSetupIntent(intent.pullRefreshSetup)
                }

                is TopScreenIntent.LoadingState -> {
                    onLoadingState(intent.state)
                }
            }
        }
    }
}