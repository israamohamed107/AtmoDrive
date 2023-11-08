package com.israa.atmodrive.home.data.models

data class OnTripResponse(
    val message: String,
    val status: Boolean,
    val trip_id: Int?
)
