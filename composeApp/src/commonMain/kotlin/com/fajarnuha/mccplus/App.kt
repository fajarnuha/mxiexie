package com.fajarnuha.mccplus

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var isLogin by remember { mutableStateOf(false) }
        when {
            isLogin -> MainScreen()
            else -> {
                LoginScreen(onSuccess = {
                    isLogin = true
                })
            }
        }
    }
}