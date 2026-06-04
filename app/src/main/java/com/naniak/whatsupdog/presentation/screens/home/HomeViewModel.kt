package com.naniak.whatsupdog.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.usecase.GetRandomDogUseCase
import com.naniak.whatsupdog.domain.usecase.IsFavoriteUseCase
import com.naniak.whatsupdog.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getRandomDogUseCase: GetRandomDogUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dogHistory = mutableListOf<DogImage>()
    private var currentIndex = -1
    private var favoriteJob: Job? = null

    init {
        loadRandomDog()
    }

    fun loadRandomDog() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getRandomDogUseCase()
                .onSuccess { dogImage ->
                    // Remove any forward history when loading a new dog
                    if (currentIndex < dogHistory.lastIndex) {
                        dogHistory.subList(currentIndex + 1, dogHistory.size).clear()
                    }
                    dogHistory.add(dogImage)
                    currentIndex = dogHistory.lastIndex
                    showDogAtCurrentIndex()
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(
                        message = error.message ?: "Failed to load a random dog"
                    )
                }
        }
    }

    fun goToPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            showDogAtCurrentIndex()
        }
    }

    fun goToNext() {
        if (currentIndex < dogHistory.lastIndex) {
            currentIndex++
            showDogAtCurrentIndex()
        } else {
            loadRandomDog()
        }
    }

    private fun showDogAtCurrentIndex() {
        val dogImage = dogHistory[currentIndex]
        _uiState.value = HomeUiState.Success(
            dogImage = dogImage,
            canGoBack = currentIndex > 0
        )
        observeFavoriteStatus(dogImage.imageUrl)
    }

    private fun observeFavoriteStatus(imageUrl: String) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            isFavoriteUseCase(imageUrl).collectLatest { isFavorite ->
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(isFavorite = isFavorite)
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            viewModelScope.launch {
                toggleFavoriteUseCase(currentState.dogImage)
            }
        }
    }
}
