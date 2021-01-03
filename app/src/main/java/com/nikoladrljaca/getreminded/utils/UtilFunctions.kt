package com.nikoladrljaca.getreminded.utils

import androidx.core.graphics.toColorInt
import java.text.SimpleDateFormat
import java.util.*

fun dateFromEpoch(epoch: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    val date = Date(epoch)
    return sdf.format(date)
}