package com.simenko.qmapp.ui.common.animation

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.Dp

interface HorizonteAnimation {
    fun getRequiredScreenWidth(isSecondRowVisible: Int): Triple<Dp, Dp, Dp>
    fun setRequiredScreenWidth(isSecondRowVisible: Int, action: (Triple<Dp, Dp, Dp>) -> Unit)
    fun ScrollState.animateScroll(isSecondRowVisible: Int)

    fun setBoolean(value: Boolean, action: (Boolean) -> Unit)
}