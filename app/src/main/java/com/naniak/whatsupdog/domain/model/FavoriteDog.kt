package com.naniak.whatsupdog.domain.model

data class FavoriteDog(
    val id: Long = 0,
    val imageUrl: String,
    val breed: String? = null,
    val savedAt: Long = System.currentTimeMillis()
)
