package com.naniak.whatsupdog.domain.repository

import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.model.DogImage

interface DogRepository {
    suspend fun getRandomDog(): Result<DogImage>
    suspend fun getAllBreeds(): Result<List<Breed>>
    suspend fun getBreedImages(breed: String): Result<List<DogImage>>
}
