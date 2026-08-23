package com.example.chintualarm.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import chintualarm.shared.generated.resources.Res
import chintualarm.shared.generated.resources.alarm
import chintualarm.shared.generated.resources.alarm_remind
import chintualarm.shared.generated.resources.check
import chintualarm.shared.generated.resources.close
import chintualarm.shared.generated.resources.keyboard
import chintualarm.shared.generated.resources.label
import chintualarm.shared.generated.resources.repeat
import chintualarm.shared.generated.resources.vibrate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

class AddUpdateAlarmScreen : Screen {

    @Composable
    override fun Content() {
        AddUpdateAlarmView()
    }
}

@Composable
private fun AddUpdateAlarmView() {
    val isVibrateEnable = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                TopBarView(onBackPressed = {}, onDonePressed = {})

                HorizontalDivider(modifier = Modifier.padding(bottom = 20.dp))
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(Res.drawable.keyboard),
                        modifier = Modifier.size(50.dp).padding(end = 24.dp),
                        contentDescription = ""
                    )
                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.repeat,
                    label = "Repeat",
                    value = "Only Once"
                ) {

                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.alarm,
                    label = "Alarm Sound",
                    value = "Default ringtone (Fine Day)"
                ) {

                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.label,
                    label = "Label",
                    value = "Label"
                ) {

                }
            }

            item {
                KeyValueRowView(
                    img = Res.drawable.alarm_remind,
                    label = "Remind Later",
                    value = "5 Minutes"
                ) {

                }
            }

            item {
                VibrateToggleRowView(
                    isVibrateEnable = isVibrateEnable
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
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(img),
            modifier = Modifier.size(50.dp).padding(end = 24.dp),
            contentDescription = ""
        )

        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = label ?: "",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun VibrateToggleRowView(isVibrateEnable: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.vibrate),
            modifier = Modifier.size(50.dp).padding(end = 24.dp),
            contentDescription = ""
        )

        Text(
            text = "Vibrate",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isVibrateEnable.value,
            onCheckedChange = {
                isVibrateEnable.value = it
            }
        )
    }
}

@Composable
fun TopBarView(onBackPressed: () -> Unit = {}, onDonePressed: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.close),
            modifier = Modifier
                .size(50.dp)
                .padding(end = 24.dp)
                .clickable {
                    onBackPressed()
                },
            contentDescription = "",
        )

        Text(
            text = "Set alarm",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(Res.drawable.check),
            modifier = Modifier
                .size(50.dp)
                .padding(end = 24.dp)
                .clickable {
                    onDonePressed()
                },
            contentDescription = ""
        )
    }
}

// Preview
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AddUpdateAlarmScreenPreview() {
    MaterialTheme {
        AddUpdateAlarmView()
    }
}

