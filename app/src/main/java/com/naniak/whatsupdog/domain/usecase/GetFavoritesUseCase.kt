package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<FavoriteDog>> {
        return repository.getAllFavorites()
    }
}
