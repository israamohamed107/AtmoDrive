package com.israa.atmodrive.home.domain.usecase

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.data.models.CancelTripResponse
import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.data.models.TripData
import com.israa.atmodrive.home.domain.repo.IHomeRepository
import javax.inject.Inject

class HomeUseCase @Inject constructor(private val iHomeRepository: IHomeRepository) : IHomeUseCase {
    override suspend fun onTrip(): ResponseState<TripData> = iHomeRepository.onTrip()

    override suspend fun makeTrip(
        distanceText: String,
        distanceValue: Double,
        durationText: String,
        durationValue: Int
    ): ResponseState<MakeTripResponse> =
        iHomeRepository.makeTrip(distanceText, distanceValue, durationText, durationValue)


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
    ) =
        iHomeRepository.confirmTrip(
            vehicleClassId,
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

    override suspend fun getCaptainDetails(tripId: Long): ResponseState<CaptainDetailsResponse> =
        iHomeRepository.getCaptainDetails(tripId)

    override suspend fun cancelTrip(tripId: Long): ResponseState<CancelTripResponse> = iHomeRepository.cancelTrip(tripId)
    override suspend fun cancelBeforeCaptainAccept(tripId: Long): ResponseState<CancelTripResponse> =
        iHomeRepository.cancelBeforeCaptainAccept(tripId)

}