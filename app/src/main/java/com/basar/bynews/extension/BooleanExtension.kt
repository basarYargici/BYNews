package com.basar.bynews.extension

fun Boolean?.isTrue() = this == true

fun Boolean?.isNullOrTrue() = this == null || this == true

fun Boolean?.isFalse() = this == false

fun Boolean?.takeIfFalse(predicate: () -> Unit) {
    if (this == false) {
        predicate.invoke()
    }
}

fun Boolean?.takeIfTrue(predicate: () -> Unit) {
    if (this == true) {
        predicate.invoke()
    }
}

fun Boolean?.isNullOrFalse() = this == null || this == false


fun Boolean?.orFalse(): Boolean = this ?: false
fun Boolean?.orTrue(): Boolean = this ?: true
