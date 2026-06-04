package com.naniak.whatsupdog.presentation.screens.breeds

import com.naniak.whatsupdog.domain.model.Breed

sealed interface BreedsUiState {
    data object Loading : BreedsUiState
    data class Success(
        val breeds: List<Breed>,
        val query: String = ""
    ) : BreedsUiState
    data class Error(val message: String) : BreedsUiState
}
