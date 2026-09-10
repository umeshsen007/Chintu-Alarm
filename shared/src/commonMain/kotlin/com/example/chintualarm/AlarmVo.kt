package com.example.chintualarm

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class AlarmItemDto(
    val id: String = Random.nextLong().toString(),
    var hour: Int = 8,
    var minute: Int = 30,
    var weeks: List<Int> = emptyList(), // 0 = Sun, 1 = Mon, ... 6 = Sat
    var alarmSound: String = "Default",
    var label: String? = null,
    var vibrate: Boolean = false,
    var isAlarmActive: Boolean = true,
    var remindLater: Int = 5,
) {
    val timeStr: String
        get() {
            val isPm = hour >= 12
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val amPm = if (isPm) "PM" else "AM"
            val minuteStr = minute.toString().padStart(2, '0')
            return "$displayHour:$minuteStr $amPm"
        }
}