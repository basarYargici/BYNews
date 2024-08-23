package com.basar.bynews.extension

fun Int?.orZero(): Int = this ?: 0
fun Int.isZero(): Boolean = this == 0

fun Int.isBetween(start: Int, end: Int): Boolean {
    return this in start..end
}