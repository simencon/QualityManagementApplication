package com.simenko.qmapp.ui.dialogs

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.simenko.qmapp.domain.NoRecord

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event -> eventHandler.value(owner, event) }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}

suspend fun <S : ScrollableState, T> S.scrollToSelectedItem(list: List<T>, selectedId: T) = list.indexOf(selectedId).let { index ->
    if (index != NoRecord.num.toInt())
        when (this) {
            is LazyGridState -> (this as LazyGridState).animateScrollToItem(index = index)
            is LazyListState -> (this as LazyListState).scrollToItem(index = index)
            else -> {}
        }
}