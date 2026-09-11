package com.example.chintualarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chintualarm.AlarmItemDto
import com.example.chintualarm.domain.AlarmRepository

class DashboardViewModel : ViewModel() {
    val alarmList = AlarmRepository.alarmList

    fun addAlarm(alarm: AlarmItemDto) {
        AlarmRepository.addAlarm(alarm)
    }

    fun updateAlarm(alarm: AlarmItemDto) {
        AlarmRepository.updateAlarm(alarm)
    }

    fun deleteAlarm(alarmId: String) {
        AlarmRepository.deleteAlarm(alarmId)
    }

    fun toggleAlarmState(alarmId: String, isActive: Boolean) {
        AlarmRepository.toggleAlarmState(alarmId, isActive)
    }
}