package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.FavoriteRepository

class ToggleFavoriteUseCase(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(dogImage: DogImage) {
        repository.toggleFavorite(dogImage)
    }
}
