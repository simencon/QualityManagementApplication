package com.simenko.qmapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.utils.BaseOrderFilter
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

    override fun trySendAddEditMode(addEditMode: Pair<AddEditMode, () -> Unit>, refreshAction: () -> Unit, filterAction: (BaseOrderFilter) -> Unit) {
        topScreenChannel.trySend(
            TopScreenIntent.TopScreenState(addEditMode.first, addEditMode.second, refreshAction, filterAction)
        )
    }
}

@Composable
fun StateChangedEffect(
    topScreenChannel: Channel<TopScreenIntent>,
    onLoadingStateIntent: (Pair<Boolean, String?>) -> Unit,
    onAddEditModeIntent: (AddEditMode, () -> Unit, () -> Unit, (BaseOrderFilter) -> Unit) -> Unit = { _, _, _, _ -> },
    onEndOfListIntent: (Boolean) -> Unit = {}
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

                is TopScreenIntent.TopScreenState -> {
                    onAddEditModeIntent(intent.addEditMode, intent.addEditAction, intent.refreshAction, intent.filterAction)
                }
            }
        }
    }
}