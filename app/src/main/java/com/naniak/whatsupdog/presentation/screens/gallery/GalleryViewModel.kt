package com.naniak.whatsupdog.presentation.screens.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.usecase.GetBreedImagesUseCase
import com.naniak.whatsupdog.domain.usecase.IsFavoriteUseCase
import com.naniak.whatsupdog.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBreedImagesUseCase: GetBreedImagesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val breed: String = savedStateHandle.get<String>("breed") ?: ""

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _uiState.value = GalleryUiState.Loading
            getBreedImagesUseCase(breed)
                .onSuccess { images ->
                    _uiState.value = GalleryUiState.Success(
                        images = images,
                        breed = breed
                    )
                    observeFavoriteStatuses(images)
                }
                .onFailure { error ->
                    _uiState.value = GalleryUiState.Error(
                        message = error.message ?: "Failed to load images for $breed"
                    )
                }
        }
    }

    private fun observeFavoriteStatuses(images: List<DogImage>) {
        images.forEach { image ->
            viewModelScope.launch {
                isFavoriteUseCase(image.imageUrl).collect { isFavorite ->
                    val currentState = _uiState.value
                    if (currentState is GalleryUiState.Success) {
                        val updatedStatuses = currentState.favoriteStatuses.toMutableMap()
                        updatedStatuses[image.imageUrl] = isFavorite
                        _uiState.value = currentState.copy(favoriteStatuses = updatedStatuses)
                    }
                }
            }
        }
    }

    fun toggleFavorite(dogImage: DogImage) {
        viewModelScope.launch {
            toggleFavoriteUseCase(dogImage)
        }
    }
}
