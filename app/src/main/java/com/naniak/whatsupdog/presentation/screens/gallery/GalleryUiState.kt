package com.naniak.whatsupdog.presentation.screens.gallery

import com.naniak.whatsupdog.domain.model.DogImage

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(
        val images: List<DogImage>,
        val breed: String,
        val favoriteStatuses: Map<String, Boolean> = emptyMap()
    ) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}
