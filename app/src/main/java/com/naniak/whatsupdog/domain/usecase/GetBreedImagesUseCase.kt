package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.DogRepository

class GetBreedImagesUseCase(
    private val repository: DogRepository
) {
    suspend operator fun invoke(breed: String): Result<List<DogImage>> {
        return repository.getBreedImages(breed)
    }
}
