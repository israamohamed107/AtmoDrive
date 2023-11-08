package com.israa.atmodrive.home.data.datasource

import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.data.models.ConfirmTripResponse
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.data.models.OnTripResponse
import com.israa.atmodrive.utils.CONFIRM_TRIP
import com.israa.atmodrive.utils.GET_CAPTAIN_DETAILS
import com.israa.atmodrive.utils.MAKE_TRIP
import com.israa.atmodrive.utils.ON_TRIP
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface HomeApiServices {

    @GET(ON_TRIP)
    suspend fun onTrip(): OnTripResponse

    @POST(MAKE_TRIP)
    @FormUrlEncoded
    suspend fun makeTrip(
        @Field("distance_text") distanceText: String,
        @Field("distance_value") distanceValue: Double,
        @Field("duration_text") durationText: String,
        @Field("duration_value") durationValue: Int
    ): MakeTripResponse

    @POST(CONFIRM_TRIP)
    @FormUrlEncoded
    suspend fun confirmTrip(
        @Field("vehicle_class_id") vehicleClassId: String,
        @Field("pickup_lat") pickupLat: String,
        @Field("pickup_lng") pickupLng: String,
        @Field("dropoff_lat") dropoffLat: String,
        @Field("dropoff_lng") dropoffLng: String,
        @Field("estimate_cost") estimateCost: String,
        @Field("estimate_time") estimateTime: String,
        @Field("estimate_distance") estimateDistance: String,
        @Field("pickup_location_name") pickupLocationName: String,
        @Field("dropoff_location_name") dropoffLocationName: String
    ): ConfirmTripResponse

    @POST(GET_CAPTAIN_DETAILS)
    @FormUrlEncoded
    suspend fun getCaptainDetails(
        @Field("trip_id") tripId: Int
    ): CaptainDetailsResponse
}