package com.naniak.whatsupdog.domain.model

data class Breed(
    val name: String,
    val subBreeds: List<String> = emptyList()
)
