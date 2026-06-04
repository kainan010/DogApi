package com.naniak.whatsupdog.presentation.screens.favorites

import com.naniak.whatsupdog.domain.model.FavoriteDog

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val favorites: List<FavoriteDog>) : FavoritesUiState
    data object Empty : FavoritesUiState
}
