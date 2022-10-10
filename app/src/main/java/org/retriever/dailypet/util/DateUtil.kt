package org.retriever.dailypet.util

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): String {
    val currentTime = System.currentTimeMillis()
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))

    return simpleDateFormat.format(currentTime)
}