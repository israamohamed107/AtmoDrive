package com.israa.atmodrive.home.data.models

data class TripData(
    val car_brand: String,
    val car_model: String,
    val dropoff_lat: String,
    val dropoff_lng: String,
    val dropoff_location_name: String,
    val pickup_lat: String,
    val pickup_lng: String,
    val pickup_location_name: String,
    val start_date: String,
    val trip_id: Long,
    val trip_status: String
)