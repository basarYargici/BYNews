package com.basar.bynews.extension

fun Any?.isNull(): Boolean {
    return this == null
}

fun Any?.isNotNull(): Boolean {
    return !this.isNull()
}

fun List<*>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}
