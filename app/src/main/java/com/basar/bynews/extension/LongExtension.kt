package com.basar.bynews.extension

import kotlin.math.pow
import kotlin.math.round

fun Long.millisToRoundedSeconds(decimals: Int = 2): Double {
    val factor = 10.0.pow(decimals)
    return round(this / 1000.0 * factor) / factor
}
