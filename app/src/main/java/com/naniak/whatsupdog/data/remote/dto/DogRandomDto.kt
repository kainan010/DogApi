package com.naniak.whatsupdog.data.remote.dto

import com.naniak.whatsupdog.domain.model.DogImage
import kotlinx.serialization.Serializable

@Serializable
data class DogRandomDto(
    val message: String,
    val status: String
) {
    fun toDomainModel(): DogImage {
        val breed = message.split("/").let { parts ->
            val breedIndex = parts.indexOf("breeds")
            if (breedIndex != -1 && breedIndex + 1 < parts.size) parts[breedIndex + 1] else null
        }
        return DogImage(imageUrl = message, breed = breed)
    }
}
