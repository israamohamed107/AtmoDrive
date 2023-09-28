package com.israa.atmodrive.auth.data.model

data class User(
    val avatar: String?,
    val email: String?,
    val full_name: String?,
    val is_dark_mode: Int,
    val lang: String,
    val mobile: String?,
    val options: CheckCodeOptions,
    val passenger_code: String,
    val rate: Int,
    val remember_token: String,
    val shake_phone: Int,
    val status: Int,
    val `suspend`: Int
)