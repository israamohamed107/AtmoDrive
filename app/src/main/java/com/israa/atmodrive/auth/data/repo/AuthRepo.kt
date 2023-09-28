package com.israa.atmodrive.auth.data.repo

import MySharedPreferences
import com.israa.atmodrive.auth.data.datasource.remote.IRemoteDataSource
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode
import com.israa.atmodrive.auth.domain.repo.IAuthRepo
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource
) : IAuthRepo {
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> =
        remoteDataSource.sendCode(mobile)

    override suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode> {
        val response = remoteDataSource.checkCode(mobile, verificationCode, deviceToken)
        return when(response){
            is ResponseState.Success ->{
                if(!response.data.isNew)
                    MySharedPreferences.setUser(response.data)
                response
            }
            is ResponseState.Failure -> {
                response
            }
        }
    }

    override suspend fun registerPassenger(
        fullName: String,
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        email: String?
    ): ResponseState<CheckCode> {
        val response = remoteDataSource.registerPassenger(
            fullName,
            mobile,
            avatar,
            deviceToken,
            deviceId,
            deviceType,
            email
        )
        return when (response) {
            is ResponseState.Success -> {
                MySharedPreferences.setUser(response.data)
                response
            }

            is ResponseState.Failure -> {
                response
            }
        }
    }
}




