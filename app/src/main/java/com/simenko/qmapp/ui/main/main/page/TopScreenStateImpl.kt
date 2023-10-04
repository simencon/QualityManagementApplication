package com.simenko.qmapp.ui.main.main.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.ui.main.main.page.components.FabSetup
import com.simenko.qmapp.ui.main.main.page.components.PullRefreshSetup
import com.simenko.qmapp.ui.main.main.page.components.TopTabsSetup
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class TopScreenStateImpl @Inject constructor() : TopScreenState {
    override val topScreenChannel: Channel<TopScreenIntent> = Channel<TopScreenIntent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override fun trySendEndOfListState(state: Boolean) {
        topScreenChannel.trySend(
            TopScreenIntent.EndOfListState(state)
        )
    }

    override fun trySendTopBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>) {
        topScreenChannel.trySend(
            TopScreenIntent.TabBadgeState(tabIndex, state)
        )
    }

    override fun trySendTopScreenSetupDev(
        mainPage: MainPage,
        onNavBtnClick: (suspend (Boolean) -> Unit)?,
        onSearchBtnClick: ((Boolean) -> Unit)?,
        onSearchAction: ((BaseFilter) -> Unit)?,
        onActionBtnClick: ((Boolean) -> Unit)?,
        onTabSelectAction: ((SelectedNumber) -> Unit)?
    ) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenSetup(
                titleSetup = TopBarSetup(mainPage, onNavBtnClick, onSearchBtnClick, onSearchAction, onActionBtnClick),
                topTabsSetup = TopTabsSetup(mainPage, onTabSelectAction)
            )
        )
    }

    override fun trySendTopScreenFabSetup(mainPage: MainPage, fabAction: (() -> Unit)?) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenFabSetup(
                fabSetup = FabSetup(mainPage, fabAction)
            )
        )
    }

    override suspend fun sendPullRefreshSetup(refreshAction: (() -> Unit)?) {
        topScreenChannel.send(
            TopScreenIntent.TopScreenPullRefreshSetup(
                pullRefreshSetup = PullRefreshSetup(refreshAction)
            )
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

    onTopScreenSetupDevIntent: (TopBarSetup, TopTabsSetup) -> Unit = { _, _ -> },
    onTopBadgeStateIntent: (Int, Triple<Int, Color, Color>) -> Unit = { _, _ -> },

    onTopScreenFabSetupIntent: (FabSetup) -> Unit = {},
    onEndOfListIntent: (Boolean) -> Unit = {},

    onTopScreenPullRefreshSetupIntent: (PullRefreshSetup) -> Unit = {},
    onLoadingState: (Pair<Boolean, String?>) -> Unit = {}
) {
    LaunchedEffect(topScreenChannel) {
        topScreenChannel.receiveAsFlow().collect { intent ->
            when (intent) {
                is TopScreenIntent.TopScreenSetup -> {
                    onTopScreenSetupDevIntent(intent.titleSetup, intent.topTabsSetup)
                }

                is TopScreenIntent.TabBadgeState -> {
                    onTopBadgeStateIntent(intent.tabIndex, intent.state)
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