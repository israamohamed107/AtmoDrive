package com.israa.atmodrive.home.viewmodels

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.domain.usecase.HomeUseCase
import com.israa.atmodrive.utils.LocationHelper
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeUseCase: HomeUseCase) : ViewModel() {


    val TAG = "HomeViewModel"
    private var _chooseFromMap = MutableStateFlow(false)
    val chooseFromMap: StateFlow<Boolean> = _chooseFromMap

    private var _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput

    private var _pickUpLocation = MutableStateFlow<LatLng?>(null)
    val pickUpLocation: StateFlow<LatLng?> = _pickUpLocation


    private var _dropOffLocation = MutableStateFlow<LatLng?>(null)
    val dropOffLocation: StateFlow<LatLng?> = _dropOffLocation

    private var _makeTrip = MutableSharedFlow<UiState?>()
    val makeTrip: SharedFlow<UiState?> = _makeTrip

    private var _passengerOnTrip = MutableStateFlow<UiState>(UiState.Loading)
    val passengerOnTrip: StateFlow<UiState> = _passengerOnTrip

    private var _confirmTrip = MutableStateFlow<UiState?>(null)
    val confirmTrip: StateFlow<UiState?> = _confirmTrip

    private var _tripStatus = MutableStateFlow("")
    val tripStatus: StateFlow<String> = _tripStatus

    private var _tripID = MutableStateFlow<Int?>(null)
    val tripID: StateFlow<Int?> = _tripID

    private var _captainDetails = MutableStateFlow<UiState?>(null)
    val captainDetails: StateFlow<UiState?> = _captainDetails
    fun setPickUpLocation(latLng: LatLng) {
        _pickUpLocation.value = latLng
    }

    fun setCurrentInput(currentInput: String) {
        _currentInput.value = currentInput
    }

    fun setDropOffLocation(latLng: LatLng?) {
        _dropOffLocation.value = latLng
    }

    fun setTripStatus(status:String){
        _tripStatus.value = status
    }

    fun setTripID(tripID:Int){
        _tripID.value = tripID
    }
    fun chooseFromMap(boolean: Boolean) {
        _chooseFromMap.value = boolean
    }

    fun getAddress(context: Context, latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address: Address?
        var addressText = ""

        val addresses: List<Address>? =
            latLng.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

        addresses?.let {
            if (addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            }
        }

        return addressText
    }

    fun onTrip() {
        viewModelScope.launch {
            when (val result = homeUseCase.onTrip()) {
                is ResponseState.Failure -> _passengerOnTrip.value = UiState.Failure(result.error)
                is ResponseState.Success -> _passengerOnTrip.value =
                    UiState.Success(result.data.status)
            }
        }
    }

    fun makeTrip() {
        val distance =
            LocationHelper.getEstimatedDistance(pickUpLocation.value!!, dropOffLocation.value!!)
        val duration =
            LocationHelper.getEstimatedTime(pickUpLocation.value!!, dropOffLocation.value!!)

        Log.i(TAG, "makeTrip: $distance")
        Log.i(TAG, "makeTrip: ${pickUpLocation.value}")
        Log.i(TAG, "makeTrip: ${dropOffLocation.value}")

        viewModelScope.launch {
            _makeTrip.emit(UiState.Loading)
            when (val result =
                homeUseCase.makeTrip("$distance KM", distance * 1000, "$duration min", duration)) {
                is ResponseState.Failure -> _makeTrip.emit(UiState.Failure(result.error))
                is ResponseState.Success -> _makeTrip.emit(UiState.Success(result.data))
            }
        }
    }

    fun confirmTrip(
        context: Context
    ) {
        viewModelScope.launch {
            _confirmTrip.value = UiState.Loading
            when(val result = homeUseCase.confirmTrip(
                "2",
                "29.9953164",//_pickUpLocation.value!!.latitude.toString(),
                "29.9953164",//_pickUpLocation.value!!.longitude.toString(),
                "32.1234",//_dropOffLocation.value!!.latitude.toString(),
                "30.1234",//_pickUpLocation.value!!.longitude.toString(),
                "50",
                "15",
                "5000",
                "cjnnknknkfkkkkkfj",//getAddress(context,_pickUpLocation.value!!),
                "cjnnknknkfkkkkkfj"//getAddress(context,_pickUpLocation.value!!)
            )){
                is ResponseState.Failure -> _confirmTrip.value = UiState.Failure(result.error)
                is ResponseState.Success -> _confirmTrip.value = UiState.Success(result.data)
            }
        }
    }

    fun getCaptainDetails(tripID: Int){
       viewModelScope.launch {
           when(val result =  homeUseCase.getCaptainDetails(tripID)){
               is ResponseState.Failure -> _captainDetails.value = UiState.Failure(result.error)
               is ResponseState.Success -> _captainDetails.value = UiState.Success(result.data)
           }
       }
    }

}