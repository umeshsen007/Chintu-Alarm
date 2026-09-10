package com.example.chintualarm

expect class AlarmScheduler() {
    fun scheduleAlarm(alarm: AlarmItemDto)
    fun cancelAlarm(alarmId: String)
}
