package com.israa.atmodrive.home.domain.usecase

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.data.models.CancelTripResponse
import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.data.models.ConfirmTripResponse
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.data.models.TripData

interface IHomeUseCase {
    suspend fun onTrip():ResponseState<TripData>

    suspend fun makeTrip(
        distanceText: String,
        distanceValue: Double,
        durationText: String,
        durationValue: Int
    ): ResponseState<MakeTripResponse>

    suspend fun confirmTrip(
        vehicleClassId: String,
        pickupLat: String,
        pickupLng: String,
        dropOffLat: String,
        dropOffLng: String,
        estimateCost: String,
        estimateTime: String,
        estimateDistance: String,
        pickupLocationName: String,
        dropOffLocationName: String
    ):ResponseState<ConfirmTripResponse>

    suspend fun getCaptainDetails(
        tripId: Long
    ): ResponseState<CaptainDetailsResponse>

    suspend fun cancelTrip(
        tripId: Long
    ): ResponseState<CancelTripResponse>

    suspend fun cancelBeforeCaptainAccept(
        tripId: Long
    ): ResponseState<CancelTripResponse>
}