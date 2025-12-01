package com.example.healthbichito.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFechaCorta(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toFechaLarga(): String {
    val sdf = SimpleDateFormat(
        "d 'de' MMMM 'de' yyyy, h:mm:ss a 'UTC-6'",
        Locale("es", "MX")
    )
    return sdf.format(Date(this))
}
