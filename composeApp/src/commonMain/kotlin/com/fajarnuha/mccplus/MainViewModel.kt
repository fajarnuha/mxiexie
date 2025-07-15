package com.fajarnuha.mccplus

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UiModel>(Loading)
    val uiState = _uiState

    fun fetch() {
        val response = mockData()
        _uiState.value = Content.from(response)
    }

    fun updateSelectedId(id: String) {
        _uiState.value = (_uiState.value as Content).copy(selectedId = id)
    }

}

sealed interface UiModel

object Loading: UiModel

data class Content(
    val access: List<Access>,
    val selectedId: String = access.first().id
): UiModel {
    data class Access(
        val id: String,
        val name: String,
        val qr: String
    )
    companion object  {
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