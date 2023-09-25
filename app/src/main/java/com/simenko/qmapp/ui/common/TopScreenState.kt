package com.simenko.qmapp.ui.common

import com.simenko.qmapp.ui.main.main.AddEditMode
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun loadingsStateChanged(loadingState: Pair<Boolean, String?>)
}

sealed class TopScreenIntent {
    data class LoadingState(
        val loadingState: Pair<Boolean, String?> = Pair(false, null)
    ) : TopScreenIntent()

    data class TopBarState(
        val addEditMode: AddEditMode
    ) : TopScreenIntent()
}