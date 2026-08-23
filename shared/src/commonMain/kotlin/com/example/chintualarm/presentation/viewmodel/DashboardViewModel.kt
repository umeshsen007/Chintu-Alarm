package com.example.chintualarm.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.chintualarm.presentation.screen.AlarmItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {
    private val _alarmList = MutableStateFlow(ArrayList<AlarmItemDto>())
    val alarmList: StateFlow<List<AlarmItemDto>> = _alarmList.asStateFlow()

    init {
        initMockData()
    }

    fun initMockData(){
        val list = ArrayList<AlarmItemDto>()
        list.add(AlarmItemDto(timeStr = "7:00 AM", label = "Morning Alarm", weeks = listOf("Mon","Tue","Wed","Sun"),isAlarmActive = false))
        list.add(AlarmItemDto(timeStr = "12:00 PM", label = "Afternoon Alarm", weeks = listOf("Mon","Wed","Sun"),isAlarmActive = true))
        list.add(AlarmItemDto(timeStr = "4:00 AM", label = "Night Alarm", weeks = listOf("Tue","Wed","Sun"),isAlarmActive = true))
        list.add(AlarmItemDto(timeStr = "10:00 PM", label = "Mid Night Alarm", weeks = listOf("Wed","Sun"),isAlarmActive = false))
        _alarmList.value.addAll(list)
    }


}