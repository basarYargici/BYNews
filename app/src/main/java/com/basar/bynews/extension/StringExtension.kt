package com.basar.bynews.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun String.formatDateAccordingToLocale(locale: Locale? = Locale("tr", "TR")): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    val dateTime = LocalDateTime.parse(this, inputFormatter)

    val outputFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(locale)

    return dateTime.format(outputFormatter)
}