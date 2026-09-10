package com.example.chintualarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chintualarm.AlarmItemDto
import com.example.chintualarm.AlarmScheduler
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DashboardViewModel : ViewModel() {
    private val settings = Settings()
    private val json = Json { ignoreUnknownKeys = true }
    private val alarmScheduler = AlarmScheduler()
    
    private val _alarmList = MutableStateFlow<List<AlarmItemDto>>(emptyList())
    val alarmList: StateFlow<List<AlarmItemDto>> = _alarmList.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        val alarmsJson = settings.getString("alarms_list", "")
        if (alarmsJson.isNotEmpty()) {
            try {
                val list = json.decodeFromString<List<AlarmItemDto>>(alarmsJson)
                _alarmList.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveAlarms(list: List<AlarmItemDto>) {
        val jsonString = json.encodeToString(list)
        settings["alarms_list"] = jsonString
    }

    fun addAlarm(alarm: AlarmItemDto) {
        _alarmList.update { currentList ->
            val newList = currentList + alarm
            saveAlarms(newList)
            newList
        }
        if (alarm.isAlarmActive) {
            alarmScheduler.scheduleAlarm(alarm)
        }
    }

    fun updateAlarm(alarm: AlarmItemDto) {
        _alarmList.update { currentList ->
            val newList = currentList.map { if (it.id == alarm.id) alarm else it }
            saveAlarms(newList)
            newList
        }
        if (alarm.isAlarmActive) {
            alarmScheduler.scheduleAlarm(alarm)
        } else {
            alarmScheduler.cancelAlarm(alarm.id)
        }
    }

    fun deleteAlarm(alarmId: String) {
        _alarmList.update { currentList ->
            val newList = currentList.filterNot { it.id == alarmId }
            saveAlarms(newList)
            newList
        }
        alarmScheduler.cancelAlarm(alarmId)
    }

    fun toggleAlarmState(alarmId: String, isActive: Boolean) {
        var toggledAlarm: AlarmItemDto? = null
        _alarmList.update { currentList ->
            val newList = currentList.map { 
                if (it.id == alarmId) {
                    val modified = it.copy(isAlarmActive = isActive)
                    toggledAlarm = modified
                    modified
                } else it 
            }
            saveAlarms(newList)
            newList
        }
        
        toggledAlarm?.let {
            if (isActive) {
                alarmScheduler.scheduleAlarm(it)
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
        }
    }
}