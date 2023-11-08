package com.israa.atmodrive.home.data.models

data class TripModel(
    var captainId:Int? = null,
    var cost:Int? = null,
    var distance:Double? = null,
    var lat:String? = null,
    var lng:String? = null,
    var passengerId:Int? = null,
    var status:String? = null,
    var tripId:Int? = null,
    var waitTime:Int? = null
)
