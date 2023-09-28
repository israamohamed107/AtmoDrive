package com.israa.atmodrive.auth.domain.repo

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode

interface IAuthRepo {

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