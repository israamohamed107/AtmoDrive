package com.israa.atmodrive.home.data.repo

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.data.datasource.HomeRemoteDataSource
import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.data.models.ConfirmTripResponse
import com.israa.atmodrive.home.data.models.OnTripResponse
import com.israa.atmodrive.home.domain.repo.IHomeRepository
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeRemoteDataSource: HomeRemoteDataSource) :
    IHomeRepository {
    override suspend fun onTrip(): ResponseState<OnTripResponse> = homeRemoteDataSource.onTrip()
    override suspend fun makeTrip(
        distanceText: String,
        distanceValue: Double,
        durationText: String,
        durationValue: Int
    ) = homeRemoteDataSource.makeTrip(distanceText, distanceValue, durationText, durationValue)

    override suspend fun confirmTrip(
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
    ): ResponseState<ConfirmTripResponse> = homeRemoteDataSource.confirmTrip(
        vehicleClassId = vehicleClassId,
        pickupLat,
        pickupLng,
        dropOffLat,
        dropOffLng,
        estimateCost,
        estimateTime,
        estimateDistance,
        pickupLocationName,
        dropOffLocationName
    )

    override suspend fun getCaptainDetails(tripId: Int): ResponseState<CaptainDetailsResponse> =
        homeRemoteDataSource.getCaptainDetails(tripId)
}