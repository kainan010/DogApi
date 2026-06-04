package com.naniak.whatsupdog.data.remote.dto

import com.naniak.whatsupdog.domain.model.Breed
import kotlinx.serialization.Serializable

@Serializable
data class BreedListDto(
    val message: Map<String, List<String>>,
    val status: String
) {
    fun toDomainModel(): List<Breed> = message.map { (name, subBreeds) ->
        Breed(name = name, subBreeds = subBreeds)
    }.sortedBy { it.name }
}
