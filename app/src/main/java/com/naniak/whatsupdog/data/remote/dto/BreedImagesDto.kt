package com.naniak.whatsupdog.data.remote.dto

import com.naniak.whatsupdog.domain.model.DogImage
import kotlinx.serialization.Serializable

@Serializable
data class BreedImagesDto(
    val message: List<String>,
    val status: String
) {
    fun toDomainModel(breed: String): List<DogImage> = message.map {
        DogImage(imageUrl = it, breed = breed)
    }
}
