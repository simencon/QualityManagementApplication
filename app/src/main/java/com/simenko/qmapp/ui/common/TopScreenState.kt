package com.simenko.qmapp.ui.common

import com.simenko.qmapp.ui.main.main.AddEditMode
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>)
    fun trySendLoadingState(loadingState: Pair<Boolean, String?>)

    fun trySendAddEditMode(addEditMode: Pair<AddEditMode, () -> Unit>)
}

sealed class TopScreenIntent {
    data class LoadingState(
        val loadingState: Pair<Boolean, String?> = Pair(false, null)
    ) : TopScreenIntent()

    data class AddEditMode(
        val addEditMode: com.simenko.qmapp.ui.main.main.AddEditMode,
        val addEditAction: () -> Unit
    ) : TopScreenIntent()
}