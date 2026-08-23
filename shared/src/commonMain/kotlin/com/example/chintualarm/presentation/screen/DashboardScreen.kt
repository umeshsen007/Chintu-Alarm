package com.example.chintualarm.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.chintualarm.presentation.viewmodel.DashboardViewModel

data class AlarmItemDto(
    var timeStr: String? = null,
    var label: String? = null,
    var weeks: List<String?>? = emptyList(),
    var isAlarmActive: Boolean
)

class DashboardScreen : Screen {

    @Composable
    override fun Content() {
        ContentView()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContentView() {
    val vm: DashboardViewModel = viewModel()
    val alarmList = vm.alarmList.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            SmallFloatingActionButton(
                modifier = Modifier.wrapContentSize(),
                onClick = {

                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Text("+ Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(items = alarmList.value) { item ->
                ItemRowView(item)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            }
        }
    }
}

@Composable
private fun ItemRowView(item: AlarmItemDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            Row(modifier = Modifier, verticalAlignment = Alignment.Bottom) {
                Text(
                    text = item.timeStr ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = item.timeStr?.takeLast(2) ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(5.dp))

            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                if (!item.label.isNullOrBlank()) {
                    Text(
                        text = item.label ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Blue,
                        modifier = Modifier.padding(end = 10.dp),
                    )
                }

                item.weeks?.forEachIndexed { index, week ->
                    Text(
                        text = buildString {
                            append(week.orEmpty())
                            if (index < item.weeks.orEmpty().lastIndex) {
                                append(", ")
                            }
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(0.2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Switch(
                checked = item.isAlarmActive,
                onCheckedChange = {
                    // todo later
                }
            )
        }
    }
}

// Preview
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DashboardScreenPreview() {
    MaterialTheme {
        ContentView()
    }
}