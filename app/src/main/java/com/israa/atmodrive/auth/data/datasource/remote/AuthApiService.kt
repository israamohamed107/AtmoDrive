package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrive.auth.data.model.CheckCodeResponse
import com.israa.atmodrive.auth.data.model.RegisterPassengerResponse
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.utils.CHECK_CODE
import com.israa.atmodrive.utils.RESISTER_PASSENGER
import com.israa.atmodrive.utils.SEND_CODE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {

    @POST(SEND_CODE)
    @FormUrlEncoded

    suspend fun sendCode(@Field("mobile") mobile: String): SendCodeResponse

    @POST(CHECK_CODE)
    @FormUrlEncoded
    suspend fun checkCode(
        @Field("mobile") mobile: String,
        @Field("verification_code") verificationCode: String,
        @Field("device_token") deviceToken: String
    ): CheckCodeResponse

    @POST(RESISTER_PASSENGER)
    @FormUrlEncoded

    suspend fun registerPassenger(
        @Field("full_name") fullName: String,
        @Field("mobile") mobile: String,
        @Field("avatar") avatar: String?,
        @Field("device_token") deviceToken: String,
        @Field("device_id") deviceId: String,
        @Field("device_type") deviceType: String,
        @Field("email") email: String?
    ): RegisterPassengerResponse

}