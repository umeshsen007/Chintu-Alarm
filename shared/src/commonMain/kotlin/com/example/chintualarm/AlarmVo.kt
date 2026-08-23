package com.example.chintualarm

data class AlarmItemDto(
    var timeStr: String? = null,
    var weeks: List<String?>? = emptyList(), // repeat
    var alarmSound: String,
    var label: String? = null,
    var vibrate: Boolean? = null,
    var isAlarmActive: Boolean? = null,
    var remindLater: Int? = null,
)