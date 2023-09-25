package com.simenko.qmapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class TopScreenStateImpl @Inject constructor() : TopScreenState {
    override val topScreenChannel: Channel<TopScreenIntent> = Channel<TopScreenIntent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    override suspend fun loadingsStateChanged(loadingState: Pair<Boolean, String?>) {
        topScreenChannel.send(
            TopScreenIntent.LoadingState(loadingState)
        )
    }
}

@Composable
fun StateChangedEffect(topScreenChannel: Channel<TopScreenIntent>, onLoadingStateChanged: (Pair<Boolean, String?>) -> Unit) {
    LaunchedEffect(topScreenChannel, onLoadingStateChanged) {
        topScreenChannel.receiveAsFlow().collect { state ->
            when (state) {
                is TopScreenIntent.LoadingState -> {
                    onLoadingStateChanged(state.loadingState)
                }

                is TopScreenIntent.TopBarState -> {

                }
            }
        }
    }
}