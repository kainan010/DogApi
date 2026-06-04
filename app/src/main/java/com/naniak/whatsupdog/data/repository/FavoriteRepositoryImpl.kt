package com.naniak.whatsupdog.data.repository

import com.naniak.whatsupdog.data.local.FavoriteDao
import com.naniak.whatsupdog.data.local.FavoriteEntity
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.model.FavoriteDog
import com.naniak.whatsupdog.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override suspend fun toggleFavorite(dogImage: DogImage) {
        val isFav = favoriteDao.isFavorite(dogImage.imageUrl).first()
        if (isFav) {
            favoriteDao.deleteByImageUrl(dogImage.imageUrl)
        } else {
            favoriteDao.insert(
                FavoriteEntity(
                    imageUrl = dogImage.imageUrl,
                    breed = dogImage.breed,
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override fun getAllFavorites(): Flow<List<FavoriteDog>> {
        return favoriteDao.getAll().map { entities ->
            entities.map { entity ->
                FavoriteDog(
                    id = entity.id,
                    imageUrl = entity.imageUrl,
                    breed = entity.breed,
                    savedAt = entity.savedAt
                )
            }
        }
    }

    override fun isFavorite(imageUrl: String): Flow<Boolean> {
        return favoriteDao.isFavorite(imageUrl)
    }
}
