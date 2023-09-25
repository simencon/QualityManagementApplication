package com.simenko.qmapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.simenko.qmapp.ui.main.main.AddEditMode
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

    override fun trySendAddEditMode(addEditMode: Pair<AddEditMode, () -> Unit>) {
        topScreenChannel.trySend(
            TopScreenIntent.AddEditMode(addEditMode.first, addEditMode.second)
        )
    }
}

@Composable
fun StateChangedEffect(
    topScreenChannel: Channel<TopScreenIntent>,
    onLoadingStateIntent: (Pair<Boolean, String?>) -> Unit,
    onAddEditModeIntent: (AddEditMode, () -> Unit) -> Unit = { _, _ -> }
) {
    LaunchedEffect(topScreenChannel, onLoadingStateIntent) {
        topScreenChannel.receiveAsFlow().collect { intent ->
            when (intent) {
                is TopScreenIntent.LoadingState -> {
                    onLoadingStateIntent(intent.loadingState)
                }

                is TopScreenIntent.AddEditMode -> {
                    onAddEditModeIntent(intent.addEditMode, intent.addEditAction)
                }
            }
        }
    }
}