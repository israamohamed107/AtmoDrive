package com.israa.atmodrive.home.data.datasource

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.data.models.ConfirmTripResponse
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.data.models.OnTripResponse
import com.israa.atmodrive.utils.explain
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(private val ihomeApiServices: HomeApiServices) :
    IHomeRemoteDataSource {
    override suspend fun onTrip(): ResponseState<OnTripResponse> {
        return try {
            val result = ihomeApiServices.onTrip()
            if (result.status) {
                ResponseState.Success(result)
            } else {
                ResponseState.Failure(result.message)
            }
        } catch (e: Exception) {
            e.explain()
        }
    }

    override suspend fun makeTrip(
        distanceText: String,
        distanceValue: Double,
        durationText: String,
        durationValue: Int
    ): ResponseState<MakeTripResponse> {
        return try {
            val result = ResponseState.Success(
                ihomeApiServices.makeTrip(
                    distanceText,
                    distanceValue,
                    durationText,
                    durationValue
                )
            )
            if (result.data.status) {
                result
            } else {
                ResponseState.Failure(result.data.message)
            }
        } catch (e: Exception) {
            e.explain()
        }
    }

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
    ): ResponseState<ConfirmTripResponse> {
        return try {
            val result = ihomeApiServices.confirmTrip(
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
            if (result.status) {
                ResponseState.Success(result)
            } else {
                ResponseState.Failure(result.message)
            }
        } catch (e: Exception) {
            e.explain()
        }
    }

    override suspend fun getCaptainDetails(tripId: Int): ResponseState<CaptainDetailsResponse> {
        return try {

        val result = ihomeApiServices.getCaptainDetails(tripId)
        if (result.status) {
            ResponseState.Success(result)
        } else {
            ResponseState.Failure(result.message)
        }
    } catch (e: Exception) {
        e.explain()
    }
    }


}