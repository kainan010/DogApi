package com.naniak.whatsupdog.data.repository

import com.naniak.whatsupdog.data.remote.DogApiService
import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.DogRepository

class DogRepositoryImpl(
    private val apiService: DogApiService
) : DogRepository {

    override suspend fun getRandomDog(): Result<DogImage> {
        return try {
            val response = apiService.getRandomDog()
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllBreeds(): Result<List<Breed>> {
        return try {
            val response = apiService.getAllBreeds()
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBreedImages(breed: String): Result<List<DogImage>> {
        return try {
            val response = apiService.getBreedImages(breed)
            Result.success(response.toDomainModel(breed))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
