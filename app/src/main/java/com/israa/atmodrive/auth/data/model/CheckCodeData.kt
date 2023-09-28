package com.israa.atmodrive.auth.data.model

data class CheckCodeData(
    val is_new: Boolean,
    val user: User?
)