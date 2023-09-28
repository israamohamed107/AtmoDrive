package com.israa.atmodrive.auth.domain.model

import com.israa.atmodrive.auth.data.model.CheckCodeOptions

data class CheckCode(
    val avatar: String? = null,
    val email: String? = null,
    val fullName: String?= null,
    val isDarkMode: Int? = null,
    val lang: String? = null,
    val mobile: String?= null,
    val passengerCode: String? = null,
    val rate: Int? = null,
    val rememberToken: String? = null,
    val shakePhone: Int? = null,
    val status: Int? = null,
    val `suspend`: Int?= null,
    val isNew:Boolean
)

