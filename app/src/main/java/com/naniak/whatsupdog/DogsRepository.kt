package com.naniak.whatsupdog

import com.naniak.whatsupdog.api.BaseRepository
import com.naniak.whatsupdog.api.DogService
import com.naniak.whatsupdog.api.ResponseApi


class DogsRepository() : BaseRepository() {

    suspend fun getRandomDogs(): ResponseApi {
        return safeApiCall { DogService.dogService.dogsRandom() }
    }
}