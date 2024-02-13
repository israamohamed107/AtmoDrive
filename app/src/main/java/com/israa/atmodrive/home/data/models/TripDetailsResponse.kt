package com.israa.atmodrive.home.data.models

data class TripDetailsResponse(
    val `data`: TripData?,
    val message: String,
    val status: Boolean
)