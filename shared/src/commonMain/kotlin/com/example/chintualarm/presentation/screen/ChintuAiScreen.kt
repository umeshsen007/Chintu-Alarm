package com.example.chintualarm.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.chintualarm.AlarmItemDto
import com.example.chintualarm.domain.AiClient
import com.example.chintualarm.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlin.random.Random

data class ChatMessage(val text: String, val isUser: Boolean)

class ChintuAiScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: DashboardViewModel = viewModel { DashboardViewModel() }
        val alarmList by vm.alarmList.collectAsState()
        
        var messageText by remember { mutableStateOf("") }
        val messages = remember { mutableStateListOf(ChatMessage("Hi, I am Chintu AI! How can I help you manage your alarms?", false)) }
        var isLoading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val aiClient = remember { AiClient() }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chintu AI") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Text("Back")
                        }
                    }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Set an alarm for 7 AM") },
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (messageText.isBlank()) return@Button
                            val msg = messageText
                            messages.add(ChatMessage(msg, true))
                            messageText = ""
                            isLoading = true
                            
                            scope.launch {
                                val currentAlarmsJson = Json.encodeToString(alarmList)
                                val currentTime = "08:00" // Dummy time for now to fix iOS compile error
                                
                                try {
                                    val actions = aiClient.parseIntent(msg, currentAlarmsJson, currentTime)
                                    if (actions != null && actions.isNotEmpty()) {
                                        for (action in actions) {
                                            when (action.action) {
                                                "ADD" -> {
                                                    val newAlarm = AlarmItemDto(
                                                        id = action.id ?: Random.nextLong().toString(),
                                                        hour = action.hour ?: 8,
                                                        minute = action.minute ?: 0,
                                                        label = action.label ?: "AI Alarm"
                                                    )
                                                    vm.addAlarm(newAlarm)
                                                }
                                                "UPDATE" -> {
                                                    if (action.id != null) {
                                                        val existing = alarmList.find { it.id == action.id }
                                                        if (existing != null) {
                                                            vm.updateAlarm(
                                                                existing.copy(
                                                                    hour = action.hour ?: existing.hour,
                                                                    minute = action.minute ?: existing.minute,
                                                                    label = action.label ?: existing.label
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                "DELETE" -> {
                                                    if (action.id != null) {
                                                        vm.deleteAlarm(action.id)
                                                    }
                                                }
                                            }
                                        }
                                        messages.add(ChatMessage(actions.last().replyToUser, false))
                                    } else {
                                        messages.add(ChatMessage("I didn't receive any actions.", false))
                                    }
                                } catch (e: Exception) {
                                    messages.add(ChatMessage("Error: ${e.message}", false))
                                }
                                isLoading = false
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Send")
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                if (isLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                    }
                }
                items(messages.reversed()) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (msg.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (msg.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
