package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrive.auth.data.datasource.mapper.asDomain
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode
import com.israa.atmodrive.utils.explain
import java.lang.Exception
import javax.inject.Inject

class RemoteDataSource @Inject constructor(val authApiService: AuthApiService) :IRemoteDataSource{
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> {
        return try {
            val response = ResponseState.Success(authApiService.sendCode(mobile))
            if(response.data.status)
                response
            else
                ResponseState.Failure(response.data.message)

        }catch (e : Exception){
            e.explain()
        }
    }


    override suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode> {
        return try {
            val response = authApiService.checkCode(
                    mobile,
                    verificationCode,
                    deviceToken)

            if (response.status)
                ResponseState.Success(response.asDomain())
            else
                ResponseState.Failure(response.message)
        } catch (e: Exception) {
            e.explain()

        }
    }

    override suspend fun registerPassenger(
        fullName: String,
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        email:String?
    ): ResponseState<CheckCode> {
        return try {
            val response = authApiService.registerPassenger(fullName, mobile, avatar, deviceToken, deviceId, deviceType,email)
            if(response.status)
                ResponseState.Success(response.asDomain())
            else
                ResponseState.Failure(response.message)
        }catch (e : Exception){
            e.explain()
        }
    }

}