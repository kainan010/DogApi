package com.naniak.whatsupdog.api

sealed class Command {
    class Loading(val value: Boolean): Command()
    class Error(val error: Int): Command()
}