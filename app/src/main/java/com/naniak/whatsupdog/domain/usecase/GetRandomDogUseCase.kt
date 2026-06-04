package com.naniak.whatsupdog.domain.usecase

import com.naniak.whatsupdog.domain.model.DogImage
import com.naniak.whatsupdog.domain.repository.DogRepository

class GetRandomDogUseCase(
    private val repository: DogRepository
) {
    suspend operator fun invoke(): Result<DogImage> {
        return repository.getRandomDog()
    }
}
