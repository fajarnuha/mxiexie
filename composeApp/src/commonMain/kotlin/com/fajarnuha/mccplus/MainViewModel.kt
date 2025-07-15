package com.fajarnuha.mccplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajarnuha.mccplus.data.local.SettingsRepository
import com.fajarnuha.mccplus.data.local.createDataStore
import com.fajarnuha.mccplus.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository(createDataStore()),
    private val api: ApiClient = ApiClient()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiModel>(Loading)
    val uiState = _uiState

    fun fetch() {
        viewModelScope.launch {
            val response = api.getAccessData()
            if (response.isFailure) return@launch
            val accessItems = Content.from(response.getOrNull()!!.accessDataList)
            val savedId = settingsRepository.getSelectedAccess()
            val selectedId = accessItems.access.firstOrNull { it.id == savedId }?.id
                ?: accessItems.access.first().id
            _uiState.value = accessItems.copy(selectedId = selectedId)
        }
    }



    fun updateSelectedId(id: String) {
        viewModelScope.launch {
            (_uiState.value as? Content)?.let {
                _uiState.value = it.copy(selectedId = id)
                settingsRepository.setSelectedAccess(id)
            }
        }
    }

}

sealed interface UiModel

object Loading : UiModel

data class Content(
    val access: List<Access>,
    val selectedId: String? = null
) : UiModel {
    data class Access(
        val id: String,
        val name: String,
        val qr: String
    )

    companion object {
        fun from(response: List<AccessDataItem>): Content {
            return response.map {
                Access(
                    id = it.description,
                    name = it.description,
                    qr = it.token
                )
            }.let {
                Content(it)
            }
        }
    }

}