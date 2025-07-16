package com.fajarnuha.mccplus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.fajarnuha.mccplus.ui.PopupToast

@Composable
fun LoginScreen(onSuccess: () -> Unit, viewModel: LoginViewModel = viewModel { LoginViewModel() }) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // This block listens for effects from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.loginEffect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToMain -> {
                    // Handle navigation
                    onSuccess()
                }
                is LoginEffect.ShowErrorToast -> {
                    toastMessage = effect.message
                    showToast = true
                }
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 0.dp),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 0.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    viewModel.login(username, password)
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Log In")
            }
        }
    }

    PopupToast(
        message = toastMessage,
        isVisible = showToast,
        onDismiss = { showToast = false }
    )
}

@Preview()
@Composable
fun LoginScreenPreview() {
    MaterialTheme { // Apply a MaterialTheme for the preview
        LoginScreen({})
    }
}