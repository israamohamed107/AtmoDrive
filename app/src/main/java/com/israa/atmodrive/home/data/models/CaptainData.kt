package com.israa.atmodrive.home.data.models

data class CaptainData(
    val avatar: String,
    val blood_type: Any,
    val captain_code: String,
    val dropoff_lat: String,
    val dropoff_lng: String,
    val dropoff_location_name: String,
    val estimate_cost: Double,
    val estimate_time: Int,
    val full_name: String,
    val gender: String,
    val id: Int,
    val mobile: String,
    val pickup_lat: String,
    val pickup_lng: String,
    val pickup_location_name: String,
    val rate: Int,
    val trip_color: String,
    val vehicle: String,
    val vehicle_class: Any,
    val vehicle_model: String,
    val vehicle_registration_plate: String
)