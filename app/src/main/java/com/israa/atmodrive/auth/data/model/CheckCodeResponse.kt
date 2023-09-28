package com.israa.atmodrive.auth.data.model

data class CheckCodeResponse(
    val `data`: CheckCodeData,
    val message: String,
    val status: Boolean
)