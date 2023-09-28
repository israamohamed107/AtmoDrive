package com.israa.atmodrive.utils

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Exception.explain():ResponseState.Failure{
     return when (this) {
        is IOException -> {
            ResponseState.Failure("There is no internet connection")
        }
        is SocketTimeoutException -> {
            ResponseState.Failure("Bad Connection")
        }
        is HttpException -> {
            val code = this.code()
            ResponseState.Failure("HttpException  code $code")
        }
        else -> {
//                    println("NetworkState__Error ${e.localizedMessage}")
            ResponseState.Failure(this.localizedMessage)
        }
    }

}