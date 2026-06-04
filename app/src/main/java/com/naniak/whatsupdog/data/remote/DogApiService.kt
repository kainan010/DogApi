package com.naniak.whatsupdog.data.remote

import com.naniak.whatsupdog.data.remote.dto.BreedImagesDto
import com.naniak.whatsupdog.data.remote.dto.BreedListDto
import com.naniak.whatsupdog.data.remote.dto.DogRandomDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {

    @GET("breeds/image/random")
    suspend fun getRandomDog(): DogRandomDto

    @GET("breeds/list/all")
    suspend fun getAllBreeds(): BreedListDto

    @GET("breed/{breed}/images")
    suspend fun getBreedImages(@Path("breed") breed: String): BreedImagesDto
}
