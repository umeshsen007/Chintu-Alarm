package com.example.chintualarm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chintualarm.AlarmItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {
    private val _alarmList = MutableStateFlow(ArrayList<AlarmItemDto>())
    val alarmList: StateFlow<List<AlarmItemDto>> = _alarmList.asStateFlow()
}