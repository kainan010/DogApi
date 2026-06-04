package com.naniak.whatsupdog.presentation.screens.home

import com.naniak.whatsupdog.domain.model.DogImage

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val dogImage: DogImage,
        val isFavorite: Boolean = false,
        val canGoBack: Boolean = false
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
