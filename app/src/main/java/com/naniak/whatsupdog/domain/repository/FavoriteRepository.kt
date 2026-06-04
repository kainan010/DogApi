package com.naniak.whatsupdog.domain.repository

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.model.FavoriteDog
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun toggleFavorite(dogImage: DogImage)
    fun getAllFavorites(): Flow<List<FavoriteDog>>
    fun isFavorite(imageUrl: String): Flow<Boolean>
}
