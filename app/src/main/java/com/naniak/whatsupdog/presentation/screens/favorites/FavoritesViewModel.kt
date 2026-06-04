package com.naniak.whatsupdog.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.domain.usecase.GetFavoritesUseCase
import com.naniak.whatsupdog.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collectLatest { favorites ->
                _uiState.value = if (favorites.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    FavoritesUiState.Success(favorites = favorites)
                }
            }
        }
    }

    fun removeFavorite(favoriteDog: FavoriteDog) {
        viewModelScope.launch {
            val dogImage = DogImage(
                imageUrl = favoriteDog.imageUrl,
                breed = favoriteDog.breed
            )
            toggleFavoriteUseCase(dogImage)
        }
    }
}
