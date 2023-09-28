package com.israa.atmodrive.auth.domain.usecase

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode
import com.israa.atmodrive.auth.domain.repo.IAuthRepo
import javax.inject.Inject

class AuthUseCase @Inject constructor(val iRepo: IAuthRepo) : IAuthUseCase {
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> =
        iRepo.sendCode(mobile)


    override suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode> =
        iRepo.checkCode(mobile, verificationCode, deviceToken)


    override suspend fun registerPassenger(
        fullName: String,
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        email:String?
    ): ResponseState<CheckCode> =
        iRepo.registerPassenger(fullName, mobile, avatar, deviceToken, deviceId, deviceType,email)

}