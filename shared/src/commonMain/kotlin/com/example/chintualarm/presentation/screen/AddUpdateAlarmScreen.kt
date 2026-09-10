package com.example.chintualarm.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import chintualarm.shared.generated.resources.Res
import chintualarm.shared.generated.resources.alarm
import chintualarm.shared.generated.resources.alarm_remind
import chintualarm.shared.generated.resources.check
import chintualarm.shared.generated.resources.close
import chintualarm.shared.generated.resources.label
import chintualarm.shared.generated.resources.vibrate
import com.example.chintualarm.AlarmItemDto
import com.example.chintualarm.presentation.viewmodel.DashboardViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class AddUpdateAlarmScreen(val alarmId: String? = null) : Screen {
    @Composable
    override fun Content() {
        AddUpdateAlarmView(alarmId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUpdateAlarmView(alarmId: String?) {
    val vm: DashboardViewModel = viewModel()
    val navigator = LocalNavigator.currentOrThrow

    val alarmList by vm.alarmList.collectAsState()
    val existingAlarm = remember(alarmId, alarmList) {
        alarmList.find { it.id == alarmId }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = existingAlarm?.hour ?: 8,
        initialMinute = existingAlarm?.minute ?: 30,
        is24Hour = false
    )

    var selectedDays by remember { mutableStateOf(existingAlarm?.weeks?.toSet() ?: emptySet()) }
    var vibrate by remember { mutableStateOf(existingAlarm?.vibrate ?: false) }
    var label by remember { mutableStateOf(existingAlarm?.label ?: "Alarm") }
    var sound by remember { mutableStateOf(existingAlarm?.alarmSound ?: "Default ringtone") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                TopBarView(
                    onBackPressed = { navigator.pop() },
                    onDonePressed = {
                        val newAlarm = AlarmItemDto(
                            id = existingAlarm?.id ?: kotlin.random.Random.nextLong().toString(),
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            weeks = selectedDays.toList().sorted(),
                            alarmSound = sound,
                            label = label,
                            vibrate = vibrate,
                            isAlarmActive = true
                        )
                        if (existingAlarm != null) {
                            vm.updateAlarm(newAlarm)
                        } else {
                            vm.addAlarm(newAlarm)
                        }
                        navigator.pop()
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 20.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimeInput(state = timePickerState)
                }
            }

            item {
                DaySelectorRow(selectedDays = selectedDays, onDaysChanged = { selectedDays = it })
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.alarm,
                    label = "Alarm Sound",
                    value = sound
                ) {
                    // TODO: open sound picker
                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.label,
                    label = "Label",
                    value = label
                ) {
                    // TODO: open label edit dialog
                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.alarm_remind,
                    label = "Remind Later",
                    value = "5 Minutes"
                ) {
                    // TODO: open remind later dialog
                }
            }

            item {
                VibrateToggleRowView(
                    isVibrateEnable = vibrate,
                    onCheckedChange = { vibrate = it }
                )
            }
        }
    }
}

@Composable
fun DaySelectorRow(selectedDays: Set<Int>, onDaysChanged: (Set<Int>) -> Unit) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, day ->
            val isSelected = selectedDays.contains(index)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable {
                        val newSet = if (isSelected) {
                            selectedDays - index
                        } else {
                            selectedDays + index
                        }
                        onDaysChanged(newSet)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun KeyValueRowView(
    img: DrawableResource,
    label: String?,
    value: String?,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(img),
            modifier = Modifier.size(24.dp),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.size(24.dp))

        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = label ?: "",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun VibrateToggleRowView(isVibrateEnable: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.vibrate),
            modifier = Modifier.size(24.dp),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = "Vibrate",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isVibrateEnable,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun TopBarView(onBackPressed: () -> Unit = {}, onDonePressed: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.close),
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onBackPressed()
                },
            contentDescription = "Close",
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Set Alarm",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(Res.drawable.check),
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onDonePressed()
                },
            contentDescription = "Save"
        )
    }
}

// Preview
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddUpdateAlarmScreenPreview() {
    MaterialTheme {
        AddUpdateAlarmView(null)
    }
}
