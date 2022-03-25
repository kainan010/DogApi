package com.naniak.whatsupdog.api

import com.naniak.whatsupdog.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DogService {

    val dogService = dogApiClient.create(DogApi::class.java)

    const val URL = "https://dog.ceo/api/breeds/"

    val dogApiClient: Retrofit get() =
        Retrofit.Builder()
            .baseUrl(URL)
            .client(getInterceptorClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    fun getInterceptorClient(): OkHttpClient{
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG){
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val interceptor = OkHttpClient.Builder()
            .connectTimeout(5,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()


        return  interceptor
    }
}