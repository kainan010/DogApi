package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class IsFavoriteUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(imageUrl: String): Flow<Boolean> {
        return repository.isFavorite(imageUrl)
    }
}
