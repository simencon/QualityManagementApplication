package com.simenko.qmapp.utils

import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Float.dp(): Float = this * density + 0.5f

val density: Float
    get() = Resources.getSystem().displayMetrics.density

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