package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.Breed
import com.naniak.whatsupdog.domain.repository.DogRepository

class GetAllBreedsUseCase(
    private val repository: DogRepository
) {
    suspend operator fun invoke(): Result<List<Breed>> {
        return repository.getAllBreeds()
    }
}
