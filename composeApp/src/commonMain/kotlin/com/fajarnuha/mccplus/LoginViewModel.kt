package com.fajarnuha.mccplus

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajarnuha.mccplus.data.local.SettingsRepository
import com.fajarnuha.mccplus.data.local.createDataStore
import com.fajarnuha.mccplus.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository(createDataStore()),
    private val api: ApiClient = ApiClient()
) : ViewModel() {

    // Private and mutable, for use inside the ViewModel
    private val _loginEffect = MutableSharedFlow<LoginEffect>()

    // Public and immutable, for the UI to collect
    val loginEffect = _loginEffect.asSharedFlow()

    val isLogin = settingsRepository.getTokenFlow().map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val res = api.login(username, password)
            if (res.isSuccess) {
                _loginEffect.emit(LoginEffect.NavigateToMain)
            } else {
                _loginEffect.emit(
                    LoginEffect.ShowErrorToast(
                        res.exceptionOrNull()?.message ?: "Unknown error"
                    )
                )
            }
        }
    }


}

sealed interface LoginEffect {
    data object NavigateToMain : LoginEffect
    data class ShowErrorToast(val message: String) : LoginEffect
}