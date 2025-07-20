package com.fajarnuha.mccplus

import androidx.compose.ui.graphics.ImageBitmap

sealed interface ScreenUiState {
    data object Loading : ScreenUiState
    data object Succeed : ScreenUiState
    data object Error : ScreenUiState
}

data class MainUiState(
    val access: List<Access>,
    val selectedId: String?,
    val lastQrCodeBytes: ImageBitmap?,
    val state: ScreenUiState
) {

    fun isLoading() = state == ScreenUiState.Loading
    fun isError() = state == ScreenUiState.Error

    data class Access(
        val id: String,
        val name: String,
        val qr: String
    )

    companion object {
        val Default get() = MainUiState(
            access = emptyList(),
            selectedId = null,
            lastQrCodeBytes = null,
            state = ScreenUiState.Loading
        )

        fun toAccessList(response: List<AccessDataItem>) =
            response.map {
                Access(
                    id = it.description,
                    name = it.description,
                    qr = it.token
                )
            }
    }
}
