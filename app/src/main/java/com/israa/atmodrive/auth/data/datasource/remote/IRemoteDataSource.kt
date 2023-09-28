package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode

interface IRemoteDataSource {
    suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse>
    suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode>

    suspend fun registerPassenger(
        fullName: String, mobile: String, avatar: String?, deviceToken: String,
        deviceId: String, deviceType: String, email: String?
    ): ResponseState<CheckCode>

}