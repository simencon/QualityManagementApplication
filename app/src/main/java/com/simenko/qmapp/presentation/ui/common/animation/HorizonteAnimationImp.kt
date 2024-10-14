package com.simenko.qmapp.presentation.ui.common.animation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.ZeroValue
import com.simenko.qmapp.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HorizonteAnimationImp constructor(
    private val physicalScreenWidth: Int,
    private val scope: CoroutineScope,
    private val limitToResize: Int = 720
) : HorizonteAnimation {
    override fun getRequiredScreenWidth(isSecondRowVisible: Int): Triple<Dp, Dp, Dp> {
        return Triple(
            if (physicalScreenWidth > limitToResize) physicalScreenWidth.dp else (physicalScreenWidth * (1 + 0.88 * isSecondRowVisible)).dp,
            if (isSecondRowVisible == ZeroValue.num.toInt()) physicalScreenWidth.dp else {
                if (physicalScreenWidth > limitToResize) (physicalScreenWidth * 0.57).dp else physicalScreenWidth.dp
            },
            if (physicalScreenWidth > limitToResize) (physicalScreenWidth * 0.43 * isSecondRowVisible).dp else (physicalScreenWidth * 0.88 * isSecondRowVisible).dp
        )
    }

    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply {
        scope.launch { consumeEach { it.join() } }
    }

    override fun setRequiredScreenWidth(isSecondRowVisible: Int, action: (Triple<Dp, Dp, Dp>) -> Unit) {
        channel.trySend(scope.launch(start = CoroutineStart.LAZY) { action(getRequiredScreenWidth(isSecondRowVisible)) })
    }

    override fun ScrollState.animateScroll(isSecondRowVisible: Int) {
        channel.trySend(scope.launch(start = CoroutineStart.LAZY) {
            if (physicalScreenWidth <= limitToResize)
                animateScrollTo(
                    value = isSecondRowVisible * maxValue,
                    animationSpec = tween(durationMillis = Constants.ANIMATION_DURATION, easing = LinearOutSlowInEasing)
                )
        })
    }

    override fun setBoolean(value: Boolean, action: (Boolean) -> Unit) {
        channel.trySend(scope.launch(start = CoroutineStart.LAZY) { action(value) })
    }

    fun putDelay(millis: Long) {
        channel.trySend(scope.launch(start = CoroutineStart.LAZY) { delay(millis) })
    }
}