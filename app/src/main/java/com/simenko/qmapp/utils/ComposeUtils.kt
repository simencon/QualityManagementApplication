package com.simenko.qmapp.utils

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

fun Float.dp(): Float = this * density + 0.5f

val density: Float
    get() = Resources.getSystem().displayMetrics.density

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = rememberSaveable { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
//    Usage
//    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
}

fun Color.modifyColorTone(lightnessFactor: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)

    // Modify lightness component
    hsl[2] = lightnessFactor

    // Clamp lightness to valid range (0.0 to 1.0)
    hsl[2] = hsl[2].coerceIn(0f, 1f)

    // Convert back to RGB
    val modifiedArgb = ColorUtils.HSLToColor(hsl)

    return Color(modifiedArgb)
}