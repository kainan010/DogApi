package com.naniak.whatsupdog.api

import com.naniak.whatsupdog.model.DogRandomResponse
import retrofit2.Response
import retrofit2.http.GET

interface DogApi {

    @GET("image/random")
    suspend fun dogsRandom(): Response<DogRandomResponse>
}