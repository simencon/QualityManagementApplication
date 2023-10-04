package com.simenko.qmapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.main.main.TopTabsSetupImpl
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

    override suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>) {
        topScreenChannel.send(
            TopScreenIntent.LoadingState(loadingState)
        )
    }

    override fun trySendLoadingState(loadingState: Pair<Boolean, String?>) {
        topScreenChannel.trySend(
            TopScreenIntent.LoadingState(loadingState)
        )
    }

    override fun trySendEndOfListState(state: Boolean) {
        topScreenChannel.trySend(
            TopScreenIntent.EndOfListState(state)
        )
    }

    override fun trySendTopBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>) {
        topScreenChannel.trySend(
            TopScreenIntent.TopBadgeState(tabIndex, state)
        )
    }

    override fun trySendTopScreenSetup(addEditMode: Pair<AddEditMode, () -> Unit>, refreshAction: () -> Unit, filterAction: (BaseFilter) -> Unit) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenSetup(addEditMode.first, addEditMode.second, refreshAction, filterAction)
        )
    }

    override fun trySendTopScreenSetupDev(
        mainPage: MainPage,
        onNavBtnClick: (suspend (Boolean) -> Unit)?,
        onSearchBtnClick: ((Boolean) -> Unit)?,
        onSearchAction: ((BaseFilter) -> Unit)?,
        onActionBtnClick: ((Boolean) -> Unit)?,
        onTabClickAction: ((Int) -> Unit)?,
        fabAction: () -> Unit,
        refreshAction: () -> Unit
    ) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenSetupDev(
                titleSetup = TopBarSetup(mainPage, onNavBtnClick, onSearchBtnClick, onSearchAction, onActionBtnClick),
                topTabsSetup = TopTabsSetupImpl(mainPage, onTabClickAction),
                fabSetup = FabSetup(mainPage, fabAction),
                refreshAction = refreshAction
            )
        )
    }
}

@Composable
fun StateChangedEffect(
    topScreenChannel: Channel<TopScreenIntent>,
    onLoadingStateIntent: (Pair<Boolean, String?>) -> Unit,
    onEndOfListIntent: (Boolean) -> Unit = {},
    onTopBadgeStateIntent: (Int, Triple<Int, Color, Color>) -> Unit = { _, _ -> },
    onTopScreenSetupIntent: (AddEditMode, () -> Unit, () -> Unit, (BaseFilter) -> Unit) -> Unit = { _, _, _, _ -> },
    onTopScreenSetupDevIntent: (
        TopBarSetup,
        TopTabsSetupImpl,
        FabSetup,
        () -> Unit
    ) -> Unit = { _, _, _, _ -> },
) {
    LaunchedEffect(topScreenChannel, onLoadingStateIntent) {
        topScreenChannel.receiveAsFlow().collect { intent ->
            when (intent) {
                is TopScreenIntent.LoadingState -> {
                    onLoadingStateIntent(intent.loadingState)
                }

                is TopScreenIntent.EndOfListState -> {
                    onEndOfListIntent(intent.state)
                }

                is TopScreenIntent.TopBadgeState -> {
                    onTopBadgeStateIntent(intent.tabIndex, intent.state)
                }

                is TopScreenIntent.TopScreenSetup -> {
                    onTopScreenSetupIntent(intent.addEditMode, intent.addEditAction, intent.refreshAction, intent.filterAction)
                }

                is TopScreenIntent.TopScreenSetupDev -> {
                    onTopScreenSetupDevIntent(intent.titleSetup, intent.topTabsSetup, intent.fabSetup, intent.refreshAction)
                }
            }
        }
    }
}