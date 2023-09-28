package com.israa.atmodrive.auth.data.datasource.mapper

import com.israa.atmodrive.auth.data.model.CheckCodeResponse
import com.israa.atmodrive.auth.data.model.RegisterPassengerResponse
import com.israa.atmodrive.auth.domain.model.CheckCode

fun CheckCodeResponse.asDomain(

): CheckCode {

    return data.user?.let {
        CheckCode(
            it.avatar,
            it.email,
            it.full_name,
            it.is_dark_mode,
            it.lang,
            it.mobile,
            it.passenger_code,
            it.rate,
            it.remember_token,
            it.shake_phone,
            it.status,
            it.suspend,
            data.is_new
            )
    }
        ?: CheckCode(isNew = data.is_new)
}


fun RegisterPassengerResponse.asDomain(

): CheckCode{

    return data?.let {
        CheckCode(
            it.avatar,
            it.email,
            it.full_name,
            it.is_dark_mode,
            it.lang,
            it.mobile,
            it.passenger_code,
            it.rate,
            it.remember_token,
            it.shake_phone,
            it.status,
            it.suspend,
            isNew = false
        )
    } ?:CheckCode(isNew = false)
}

