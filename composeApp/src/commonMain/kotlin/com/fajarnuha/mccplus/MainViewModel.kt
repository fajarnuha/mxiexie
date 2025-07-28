package com.fajarnuha.mccplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajarnuha.mccplus.data.local.SettingsRepository
import com.fajarnuha.mccplus.data.local.createDataStore
import com.fajarnuha.mccplus.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import qrcode.QRCode
import qrcode.color.Colors

class MainViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository(createDataStore()),
    private val api: ApiClient = ApiClient()
) : ViewModel() {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(MainUiState.Default)
    val uiState get() = _uiState.asStateFlow()

    fun fetch(force: Boolean = false) {
        _uiState.update { it.copy(state = ScreenUiState.Loading) }

        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            val response = api.getAccessData(force)

            response
                .onSuccess { onFetchSucceed(it) }
                .onFailure { onFetchError() }
        }
    }

    fun updateSelectedId(id: String) {
        _uiState.update { it.copy(selectedId = id) }
        viewModelScope.launch { settingsRepository.setSelectedAccess(id) }
    }

    fun generateQrCode() {
        val selectedId = _uiState.value.selectedId ?: return
        val data = _uiState.value.access.firstOrNull { it.id == selectedId } ?: return

        launch(Dispatchers.IO) {
            val qrCode = QRCode.ofSquares()
                .withColor(Colors.BLACK)
                .withSize(10)
                .build(data.qr)

            val toImageBitmap = imageBitmapFromBytes(qrCode.renderToBytes())
            _uiState.update { it.copy(lastQrCodeBytes = toImageBitmap) }
        }
    }

    private suspend fun onFetchSucceed(res: AccessDataWrapper) {
        val accessList = MainUiState.toAccessList(res.accessDataList.sortedBy { it.description })
        val savedId = settingsRepository.getSelectedAccess()

        val selectedId = accessList
            .firstOrNull { it.id == savedId }?.id
            ?: accessList.first().id

        _uiState.update {
            it.copy(
                access = accessList,
                selectedId = selectedId,
                state = ScreenUiState.Succeed
            )
        }
    }

    private fun onFetchError() {
        _uiState.update {
            it.copy(
                state = ScreenUiState.Error
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
