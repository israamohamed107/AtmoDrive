package com.israa.atmodrive.home.data.models

data class MakeTripData(
    val estimate_cost: String,
    val estimate_distance: Double,
    val estimate_time: Int,
    val vehicle_class_icon: String,
    val vehicle_class_name: String
)