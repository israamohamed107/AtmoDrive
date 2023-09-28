package com.israa.atmodrive.auth.data.model

data class RegisterPassengerResponse(
    val `data`: RegisterResponseData?,
    val message: String,
    val status: Boolean
)