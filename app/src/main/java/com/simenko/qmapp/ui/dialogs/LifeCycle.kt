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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

suspend fun < T : ScrollableState> T.scrollToSelectedItem(
    list: List<Int>,
    selectedId: Int,
): Boolean {
    val state = this
    var result = false
    withContext(Dispatchers.Main) {
        var index = 0
        list.forEach {
            if (it == selectedId) {
                delay(200)
                when {
                    (state::class.java == LazyListState::class.java) -> {
                        (state as LazyListState).animateScrollToItem(index = index)
                        result = true
                    }
                    (state::class.java == LazyGridState::class.java) -> {
                        (state as LazyGridState).animateScrollToItem(index = index)
                        result = true
                    }
                }
            }
            index++
        }
    }
    return result
}