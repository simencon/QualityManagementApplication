package com.simenko.qmapp.ui.common

import com.simenko.qmapp.domain.SelectedNumber
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.utils.BaseOrderFilter
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>)
    fun trySendLoadingState(loadingState: Pair<Boolean, String?>)

    fun trySendEndOfListState(state: Boolean)

    fun trySendAddEditMode(
        addEditMode: Pair<AddEditMode, () -> Unit>,
        refreshAction: () -> Unit,
        filterAction: (BaseOrderFilter) -> Unit
    )
}

sealed class TopScreenIntent {
    data class LoadingState(
        val loadingState: Pair<Boolean, String?> = Pair(false, null)
    ) : TopScreenIntent()

    data class EndOfListState(
        val state: Boolean
    ) : TopScreenIntent()

    data class TopScreenState(
        val addEditMode: AddEditMode,
        val addEditAction: () -> Unit,
        val refreshAction: () -> Unit,
        val filterAction: (BaseOrderFilter) -> Unit
    ) : TopScreenIntent()
}