package com.israa.atmodrive.auth.data.model

data class SendCodeResponse(
    val message: String,
    val status: Boolean,
    val data : SendCodeData?
)