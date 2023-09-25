package com.simenko.qmapp.ui.common

import androidx.compose.ui.graphics.Color
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.utils.BaseFilter
import kotlinx.coroutines.channels.Channel

interface TopScreenState {
    val topScreenChannel: Channel<TopScreenIntent>
    suspend fun sendLoadingState(loadingState: Pair<Boolean, String?>)
    fun trySendLoadingState(loadingState: Pair<Boolean, String?>)

    fun trySendEndOfListState(state: Boolean)

    fun trySendTopBadgeState(tabIndex: Int, state: Triple<Int, Color, Color>)

    fun trySendTopScreenSetup(
        addEditMode: Pair<AddEditMode, () -> Unit>,
        refreshAction: () -> Unit,
        filterAction: (BaseFilter) -> Unit
    )
}

sealed class TopScreenIntent {
    data class LoadingState(
        val loadingState: Pair<Boolean, String?> = Pair(false, null)
    ) : TopScreenIntent()

    data class EndOfListState(
        val state: Boolean
    ) : TopScreenIntent()

    data class TopBadgeState(
        val tabIndex: Int,
        val state: Triple<Int, Color, Color>
    ) : TopScreenIntent()

    data class TopScreenSetup(
        val addEditMode: AddEditMode,
        val addEditAction: () -> Unit,
        val refreshAction: () -> Unit,
        val filterAction: (BaseFilter) -> Unit
    ) : TopScreenIntent()
}