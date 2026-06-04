package com.naniak.whatsupdog.presentation.screens.breeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.usecase.GetAllBreedsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreedsViewModel(
    private val getAllBreedsUseCase: GetAllBreedsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedsUiState>(BreedsUiState.Loading)
    val uiState: StateFlow<BreedsUiState> = _uiState.asStateFlow()

    private var allBreeds: List<Breed> = emptyList()

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.value = BreedsUiState.Loading
            getAllBreedsUseCase()
                .onSuccess { breeds ->
                    allBreeds = breeds
                    _uiState.value = BreedsUiState.Success(breeds = breeds)
                }
                .onFailure { error ->
                    _uiState.value = BreedsUiState.Error(
                        message = error.message ?: "Failed to load breeds"
                    )
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState is BreedsUiState.Success || allBreeds.isNotEmpty()) {
            val filteredBreeds = if (query.isBlank()) {
                allBreeds
            } else {
                allBreeds.filter { breed ->
                    breed.name.contains(query, ignoreCase = true) ||
                            breed.subBreeds.any { it.contains(query, ignoreCase = true) }
                }
            }
            _uiState.value = BreedsUiState.Success(
                breeds = filteredBreeds,
                query = query
            )
        }
    }
}
