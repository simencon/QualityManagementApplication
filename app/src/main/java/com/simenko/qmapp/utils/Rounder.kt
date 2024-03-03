package com.simenko.qmapp.utils

import com.simenko.qmapp.domain.ZeroDouble
import kotlin.math.roundToLong

object Rounder {
    fun withTolerance(it: Double, tol: Int): Double {
        return when (tol) {
            0 -> it.roundToLong() / 1.00
            1 -> (it * 10.00).roundToLong() / 10.00
            2 -> (it * 100.00).roundToLong() / 100.00
            3 -> (it * 1000.00).roundToLong() / 1000.00
            4 -> (it * 10000.00).roundToLong() / 10000.00
            else -> it
        }
    }

    fun withTolerance(it: String, tol: Int): Double {
        return withTolerance(it.replace(",", ".").toDouble(), tol)
    }

    fun withToleranceStr(it: Double, tol: Int): String {
        val format = if (tol == 0) "%d" else "%.${tol}f"
        val number = if (tol == 0) withTolerance(it, tol).roundToLong() else withTolerance(it, tol)
        return String.format(format, number)
    }

    fun withToleranceStr(it: String, tol: Int): String {
        val format = if (tol == 0) "%d" else "%.${tol}f"
        val number = if (tol == 0) withTolerance(it, tol).roundToLong() else withTolerance(it, tol)
        return String.format(format, number)
    }

    fun withToleranceStrCustom(it: Double, tol: Int): String {
        val tolerance = if (it % 1.00 == ZeroDouble.double) 0 else tol
        val format = if (tolerance == 0) "%d" else "%.${tolerance}f"
        val number = if (tolerance == 0) withTolerance(it, tolerance).roundToLong() else withTolerance(it, tolerance)
        return String.format(format, number)
    }

    fun getAlpha(transparency: Int): Int {
        return ("FF".toInt(radix = 16) / 100.00 * transparency).toInt()
    }
}