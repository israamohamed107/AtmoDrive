package com.israa.atmodrive.auth.data.datasource.remote

sealed class ResponseState<out T> {

    data class Success<out T> (val data : T) : ResponseState<T>()
    data class Failure(val error : String) : ResponseState<Nothing>()

}