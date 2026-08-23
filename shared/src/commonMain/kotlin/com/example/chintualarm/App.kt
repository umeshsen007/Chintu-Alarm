package com.example.chintualarm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(DashboardScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}