package com.example.chintualarm

import java.util.Calendar

actual fun getCurrentHourAndMinute(): Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}
