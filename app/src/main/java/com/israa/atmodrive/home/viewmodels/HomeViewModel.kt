package com.israa.atmodrive.home.viewmodels

import MySharedPreferences
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.home.data.models.MakeTripData
import com.israa.atmodrive.home.data.models.TripModel
import com.israa.atmodrive.home.domain.usecase.HomeUseCase
import com.israa.atmodrive.utils.LocationHelper
import com.israa.atmodrive.utils.TRIPS
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val ref: DatabaseReference
) : ViewModel() {


    val TAG = "HomeViewModel"
    private var _chooseFromMap = MutableStateFlow(false)
    val chooseFromMap: StateFlow<Boolean> = _chooseFromMap

    private var _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput

    private var _pickUpLocation = MutableStateFlow<LatLng?>(null)
    val pickUpLocation: StateFlow<LatLng?> = _pickUpLocation


    private var _dropOffLocation = MutableStateFlow<LatLng?>(null)
    val dropOffLocation: StateFlow<LatLng?> = _dropOffLocation

    private var _captainLocation = MutableStateFlow<LatLng?>(null)
    val captainLocation: StateFlow<LatLng?> = _captainLocation

    private var _makeTrip = MutableStateFlow<UiState?>(null)
    val makeTrip: StateFlow<UiState?> = _makeTrip

    private var _passengerOnTrip = MutableStateFlow<UiState?>(null)
    val passengerOnTrip: StateFlow<UiState?> = _passengerOnTrip

    private var _confirmTrip = MutableStateFlow<UiState?>(null)
    val confirmTrip: StateFlow<UiState?> = _confirmTrip

    private var _tripStatus = MutableStateFlow("")
    val tripStatus: StateFlow<String> = _tripStatus

    private var _tripID = MutableStateFlow<UiState?>(null)
    val tripID: StateFlow<UiState?> = _tripID

    private var _cancelTrip = MutableStateFlow<UiState?>(null)
    val cancelTrip: StateFlow<UiState?> = _cancelTrip

    private var _cancelBeforeCapAccept = MutableLiveData<UiState?>(null)
    val cancelBeforeCapAccept: LiveData<UiState?> = _cancelBeforeCapAccept

    private var _captainDetails = MutableStateFlow<UiState?>(null)
    val captainDetails: StateFlow<UiState?> = _captainDetails

    private lateinit var valueEventListener: ValueEventListener

    fun setPickUpLocation(latLng: LatLng) {
        _pickUpLocation.value = latLng
    }

    fun setCurrentInput(currentInput: String) {
        _currentInput.value = currentInput
    }

    fun setDropOffLocation(latLng: LatLng?) {
        _dropOffLocation.value = latLng
    }
    fun setCaptainLocation(latLng: LatLng?) {
        _captainLocation.value = latLng
    }

    fun setTripStatus(status: String) {
        _tripStatus.value = status
    }

    fun resetMakeTrip(){
        _makeTrip.value = null
    }

    fun resetConfirmTrip(){
        _confirmTrip.value = null

    }
    fun logout(){
        MySharedPreferences.clear()
        MySharedPreferences.setIsFirstRun(false)
    }
    fun setTripID(tripID: Long?) {
        _tripID.value = if(tripID != null) UiState.Success(tripID) else null
    }

    fun chooseFromMap(boolean: Boolean) {
        _chooseFromMap.value = boolean
    }

    fun observeOnTrip(tripId: String) {

            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val trip = snapshot.getValue(TripModel::class.java) !!
                        _tripStatus.value = trip.status!!
                        _captainLocation.value = LatLng(trip.lat!!.toDouble(),trip.lng!!.toDouble())
                        Log.i("trip", "snapshot: status:${trip.status}")

                    }else
                        _tripStatus.value = ""

                }
                override fun onCancelled(error: DatabaseError) {
                    _tripStatus.value = error.message
                }

            }
            ref.child(TRIPS).child(tripId).addValueEventListener(valueEventListener)


    }

    fun removeObserveOnTrip(tripId: String) {
        ref.child(TRIPS).child(tripId).removeEventListener(valueEventListener)
    }
        fun getAddress(context: Context, latLng: LatLng): String {
            val geocoder = Geocoder(context, Locale.getDefault())
            var address: Address?
            var addressText = ""

            try {

                val addresses: List<Address>? =
                    latLng.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

                addresses?.let {
                    if (addresses.isNotEmpty()) {
                        address = addresses[0]
                        addressText = address!!.getAddressLine(0)
                    }
                }
            } catch (e: Exception) {
                addressText = ""
            }

            return addressText
        }

        fun onTrip() {
            viewModelScope.launch {
                when (val result = homeUseCase.onTrip()) {
                    is ResponseState.Failure -> _passengerOnTrip.value =
                        UiState.Failure(result.error)

                    is ResponseState.Success -> {
                        val data = result.data
                        _passengerOnTrip.value =
                            UiState.Success(data)
                        _dropOffLocation.value = LatLng(data.dropoff_lat.toDouble(),data.dropoff_lng.toDouble())
                        _pickUpLocation.value = LatLng(data.pickup_lat.toDouble(),data.pickup_lng.toDouble())
                    }

                    else -> {}
                }
            }
        }

        fun makeTrip() {
            val distance =
                LocationHelper.getEstimatedDistance(pickUpLocation.value!!, dropOffLocation.value!!)
            val duration =
                LocationHelper.getEstimatedTime(pickUpLocation.value!!, dropOffLocation.value!!)



            viewModelScope.launch {
                _makeTrip.emit(UiState.Loading)
                when (val result =
                    homeUseCase.makeTrip(
                        "$distance KM",
                        distance * 1000,
                        "$duration min",
                        duration
                    )) {
                    is ResponseState.Failure -> _makeTrip.emit(UiState.Failure(result.error))
                    is ResponseState.Success -> _makeTrip.emit(UiState.Success(result.data))
                }
            }
        }

        fun confirmTrip(
            tripData: MakeTripData,
            pickupLatLng: LatLng,
            dropOffLatLng: LatLng,
            context: Context
        ) {
            viewModelScope.launch {
                _confirmTrip.value = UiState.Loading
                when (val result = homeUseCase.confirmTrip(
                    "2",
                    pickupLatLng.latitude.toString(),
                    pickupLatLng.longitude.toString(),
                    dropOffLatLng.latitude.toString(),
                    dropOffLatLng.longitude.toString(),
                    tripData.estimate_cost,
                    tripData.estimate_time.toString(),
                    tripData.estimate_distance.toString(),
                    getAddress(context, pickupLatLng),
                    getAddress(context, dropOffLatLng)
                )) {
                    is ResponseState.Failure -> _confirmTrip.value = UiState.Failure(result.error)
                    is ResponseState.Success -> _confirmTrip.value = UiState.Success(result.data)
                }
            }
        }

        fun getCaptainDetails(tripID: Long) {
            viewModelScope.launch {
                when (val result = homeUseCase.getCaptainDetails(tripID)) {
                    is ResponseState.Failure -> _captainDetails.value =
                        UiState.Failure(result.error)

                    is ResponseState.Success -> _captainDetails.value = UiState.Success(result.data)
                }
            }
        }



        fun cancelTrip(tripID: Long) {
            viewModelScope.launch {
                _cancelTrip.value = UiState.Loading
                when (val result = homeUseCase.cancelTrip(tripID)) {
                    is ResponseState.Failure -> _cancelTrip.value = UiState.Failure(result.error)
                    is ResponseState.Success -> _cancelTrip.value = UiState.Success(result.data)
                }
            }
        }

        fun cancelBeforeCaptainAccept(tripID: Long) {
            viewModelScope.launch {
                _cancelTrip.value = UiState.Loading
                when (val result = homeUseCase.cancelBeforeCaptainAccept(tripID)) {
                    is ResponseState.Failure -> _cancelBeforeCapAccept.value =
                        UiState.Failure(result.error)

                    is ResponseState.Success -> _cancelBeforeCapAccept.value =
                        UiState.Success(result.data)
                }
            }
        }



    }