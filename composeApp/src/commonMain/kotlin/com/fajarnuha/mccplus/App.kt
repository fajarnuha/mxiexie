package com.fajarnuha.mccplus

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val vm = viewModel { LoginViewModel() }
        val isLogin by vm.isLogin.collectAsStateWithLifecycle()
        when {
            isLogin -> MainScreen()
            else -> {
                LoginScreen(onSuccess = {

                }, vm)
            }
        }
    }
}