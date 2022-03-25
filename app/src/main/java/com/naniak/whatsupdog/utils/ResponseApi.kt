package com.naniak.whatsupdog.api

sealed class ResponseApi {
    class Success(var data: Any?) : ResponseApi()
    class Error(val messageId: Int) : ResponseApi()
}