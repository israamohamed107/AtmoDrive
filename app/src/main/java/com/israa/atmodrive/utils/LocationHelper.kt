package com.israa.atmodrive.utils
import com.google.android.gms.maps.model.LatLng
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
object LocationHelper {

    fun getEstimatedTime(pickupLocation: LatLng, dropOffLocation: LatLng): Int {
        val distance = getEstimatedDistance(
            pickupLocation,
            dropOffLocation
        )

        return ((distance / 50) * 60).toInt()
    }

     fun getEstimatedDistance(
       pickUpLocation:LatLng,
       dropOffLocation:LatLng
    ): Double {
        return if (pickUpLocation.latitude == dropOffLocation.latitude && pickUpLocation.longitude == dropOffLocation.longitude) {
            0.0
        } else {
            val theta = pickUpLocation.longitude - dropOffLocation.longitude
            var distance =
                sin(Math.toRadians(pickUpLocation.latitude)) * sin(
                    Math.toRadians(dropOffLocation.latitude)
                ) + cos(Math.toRadians(pickUpLocation.latitude)) * cos(
                    Math.toRadians(
                        dropOffLocation.latitude
                    )
                ) * cos(Math.toRadians(theta))
            distance = acos(distance)

            distance = Math.toDegrees(distance)
            distance *= 60 * 1.1515


            distance *= 1.609344

            distance
        }
    }


}