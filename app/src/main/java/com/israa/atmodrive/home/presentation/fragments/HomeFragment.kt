package com.israa.atmodrive.home.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.presentation.MainActivity
import com.israa.atmodrive.databinding.FragmentHomeBinding
import com.israa.atmodrive.home.data.models.ConfirmTripResponse
import com.israa.atmodrive.home.data.models.TripData
import com.israa.atmodrive.home.presentation.fragments.trip.TripFragment
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.ACCEPTED
import com.israa.atmodrive.utils.ARRIVED
import com.israa.atmodrive.utils.DROP_OFF
import com.israa.atmodrive.utils.ON_WAY
import com.israa.atmodrive.utils.PAY
import com.israa.atmodrive.utils.PENDING
import com.israa.atmodrive.utils.PICK_UP
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.START_TRIP
import com.israa.atmodrive.utils.UiState
import com.israa.atmodrive.utils.hideKeyboard
import com.israa.atmodrive.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    private var pickupMarker: Marker? = null
    private var dropOffMarker: Marker? = null
    private var carMarker: Marker? = null

    private var addPickUpMarker = false
    private var addDropOffMarker = false
    private var addCarMarker = false


    private lateinit var pickUpLocationAddress: String

    private var pickUpLocation: LatLng? = null
    private var dropOffLocation: LatLng? = null

    private var myNavHostFragment: NavHostFragment? = null
    private lateinit var baseBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var baseBottomSheetView: ConstraintLayout

    private var chooseFromMap: Boolean = false
    private var input: String = ""
    private var isBottomSheetVisible = false
    private var searchingForCaptain = false

    private val homeViewModel: HomeViewModel by activityViewModels()



    @Inject
    lateinit var ref: DatabaseReference

    private var tripStatus: String =""
    private var flag: Boolean = true

    private var tripId:String? = null
    private var findingCaptainDialog:FindingCaptainFragment? = null
    private val DIALOG_TAG = "finding captain"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.my_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        myNavHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_sheet_frag_container) as NavHostFragment

        initLocation()
        initViews()
        onClick()
        onBachPressedSetup()


    }

    private fun onTrip() {
        homeViewModel.onTrip()
    }

    private fun initLocation() {

        mFusedLocationProviderClient.let {
            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        }

        mLocationRequest.let {
            mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000) //FastestInterval
                .setMaxUpdateDelayMillis(3000) //locationMaxWaitTime
                .setMinUpdateDistanceMeters(5f)
                .build()
        }
    }

    private fun initViews() {
        baseBottomSheetView = binding.bottomSheetId.baseBottomSheet
        baseBottomSheetBehavior = BottomSheetBehavior.from(baseBottomSheetView)
        baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetVisibility(false)

        findingCaptainDialog = FindingCaptainFragment()
        findingCaptainDialog?.isCancelable = false
    }

    private fun onClick() {
        binding.apply {
            txtDestination.setOnClickListener {
                setMyNavHostFragmentGraph(R.navigation.nav_make_trip)
                bottomSheetVisibility(true)
            }

            btnDone.setOnClickListener {
                homeViewModel.chooseFromMap(false)
                if (pickUpLocation != null && dropOffLocation != null) {
                    homeViewModel.makeTrip()
                }
            }

            imgLogout.setOnClickListener {
                homeViewModel.logout()
                startActivity(Intent(requireActivity(),MainActivity::class.java))
                requireActivity().finish()
            }

//            btnCancel.setOnClickListener {
//                homeViewModel.chooseFromMap(false)
//                clearMap()
//
//            }
        }


    }

    private fun cameraBounds(firstLocation: LatLng, secondLocation: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(firstLocation)
        builder.include(secondLocation)
        //bounds for camera
        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.15).toInt() // offset from edges of the map 15% of screen
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }

    @SuppressLint("MissingPermission")
    private fun addPickUpMarker(pickUpLocation: LatLng) {
        mMap.isMyLocationEnabled = false
        if (pickupMarker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(pickUpLocation)
                title(homeViewModel.getAddress(requireContext(), pickUpLocation))
                anchor(.5f, .5f)
                //set custom icon
                icon(bitmapFromVector(requireContext(), R.drawable.pickup_marker))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 14f))

            }
            //add marker
            pickupMarker = mMap.addMarker(markerOption)//

        } else {
            //update the current marker
            pickupMarker!!.apply {
                position = pickUpLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 14f))
            }
        }
    }

    private fun clearMap() {
//        baseBottomSheetBehavior.isDraggable = true
//        homeViewModel.setDropOffLocation(null)
        dropOffMarker = null
        pickupMarker = null
        carMarker = null
        addMarkers(false,false,false)
        mMap.clear()
        binding.card.isVisible = true
        tripId?.let { homeViewModel.removeObserveOnTrip(tripId!!)}
        tripId = null
        homeViewModel.setTripID(null)
        getMyLocation()

    }

    private fun setMyNavHostFragmentGraph(navGraphId: Int) {
        val inflater = myNavHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(navGraphId)
        myNavHostFragment?.navController?.graph = graph!!
        baseBottomSheetBehavior.isDraggable = navGraphId != R.navigation.nav_trip_status

    }


    @SuppressLint("MissingPermission")
    private fun addDropOffMarkerOnMap(dropOffLocation: LatLng) {
        mMap.isMyLocationEnabled = false
        if (dropOffMarker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(dropOffLocation)
                title(homeViewModel.getAddress(requireContext(), dropOffLocation))
                anchor(.5f, .5f)
                //set custom icon
                icon(bitmapFromVector(requireContext(), R.drawable.dropoff_marker))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropOffLocation, 14f))

            }
            //add marker
            dropOffMarker = mMap.addMarker(markerOption)//

        } else {
            //update the current marker
            dropOffMarker!!.apply {
                position = dropOffLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropOffLocation, 14f))
            }
        }


    }

    private fun addCarMarkerOnMap(captainLocation: LatLng) {
        if (carMarker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(captainLocation)
                title(homeViewModel.getAddress(requireContext(), captainLocation))
                anchor(.5f, .5f)
                //set custom icon
                icon(bitmapFromVector(requireContext(), R.drawable.car))

//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(captainLocation, 14f))

            }
            //add marker
            carMarker = mMap.addMarker(markerOption)//

        } else {
            //update the current marker
            carMarker!!.apply {
                position = captainLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(captainLocation, 14f))
            }
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
            if (!success) {
                Log.e("MAP", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
        mMap = googleMap

        checkPermissions()
    }

    private fun pickLocation() {
        mMap.setOnCameraIdleListener {
            if (input == DROP_OFF) homeViewModel.setDropOffLocation(mMap.cameraPosition.target)
            else if (input == PICK_UP) homeViewModel.setPickUpLocation(mMap.cameraPosition.target)
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            locationChecker()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        mMap.isMyLocationEnabled = true
        val currentLocationTask: Task<Location> = mFusedLocationProviderClient!!.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        )
        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val result: Location = task.result
                pickUpLocation = LatLng(result.latitude, result.longitude)
                mMap.isMyLocationEnabled = true
                homeViewModel.setPickUpLocation(pickUpLocation!!)
                moveToCurrentLocation(pickUpLocation!!)
            }
        }

    }

    private fun collect() {
        viewLifecycleOwner.lifecycleScope.launch{

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    homeViewModel.passengerOnTrip.collect { result ->
                        when (result) {
                            is UiState.Failure -> {
                                Progressbar.dismiss()
                                if (searchingForCaptain) searchingForCaptain(false)
                            }

                            UiState.Loading -> {
                                Progressbar.show(requireActivity())
                            }

                            is UiState.Success -> {
                                Progressbar.dismiss()
                                val tripDetails = result.data as TripData
                                tripId = tripDetails.trip_id.toString()
                                homeViewModel.setTripID(tripDetails.trip_id)
                                if(tripDetails.trip_status == PENDING){
                                    searchingForCaptain(true)
                                }
                                homeViewModel.observeOnTrip(tripId!!)
                                addMarkers(
                                    addCarMarker = true,
                                    addPickUpMarker = true,
                                    addDropOffMarker = true
                                )
                                binding.card.isVisible = false

                            }

                            else -> {}
                        }
                    }
                }

                launch {
                    homeViewModel.pickUpLocation.collect { currentLocation ->
                        if (currentLocation != null) {
                            pickUpLocationAddress =
                                homeViewModel.getAddress(requireContext(), currentLocation)
                            binding.txtPickup.text = pickUpLocationAddress
                            pickUpLocation = currentLocation

                            if(addPickUpMarker)
                                addPickUpMarker(currentLocation)
                        }
                    }
                }

                launch {
                    homeViewModel.dropOffLocation.collect {
                        if (it != null){
                            dropOffLocation = it
                            if (addDropOffMarker)
                                addDropOffMarkerOnMap(it)
                        }
                    }
                }
                launch {
                    homeViewModel.captainLocation.collect {
                        if (it != null){
                            addCarMarkerOnMap(it)
                            if(addPickUpMarker)
                                cameraBounds(pickUpLocation!!,it)
                            else
                                cameraBounds(dropOffLocation!!,it)
                        }
                    }
                }

                launch {
                    homeViewModel.chooseFromMap.collect {
                        if (it) {
                            pickLocation()
                            hideKeyboard()
                        }

                        chooseFromMap = it
                        chooseFromMap(it)

                    }
                }

                launch {
                    homeViewModel.currentInput.collect { currentInput ->
                        input = currentInput
                    }
                }

                launch {
                    homeViewModel.makeTrip.collect { result ->
                        when (result) {
                            is UiState.Failure -> {
                                Progressbar.dismiss()
                                Toast.makeText(
                                    requireContext(), result.message, Toast.LENGTH_SHORT
                                ).show()
                            }

                            UiState.Loading -> {
                                Progressbar.show(requireActivity())
                            }

                            is UiState.Success -> {
                                Progressbar.dismiss()
                                addDropOffMarkerOnMap(dropOffLocation!!)
                                addPickUpMarker(pickUpLocation!!)
                                cameraBounds(pickUpLocation!!, dropOffLocation!!)
                                setMyNavHostFragmentGraph(R.navigation.nav_request_trip)
                                bottomSheetVisibility(true)
                            }

                            else -> {

                            }
                        }
                    }
                }

                launch {
                    homeViewModel.confirmTrip.collect { status ->
                        when (status) {
                            is UiState.Failure -> {
                                Progressbar.dismiss()
                                showToast(status.message)
                            }

                            UiState.Loading ->
                                Progressbar.show(requireActivity())

                            is UiState.Success -> {
                                Progressbar.dismiss()
                                searchingForCaptain(true)
                                bottomSheetVisibility(false)
                                val data = status.data as ConfirmTripResponse
                                showToast(data.message)
                                tripId = data.trip_id.toString()
                                homeViewModel.setTripID(data.trip_id!!)
                                homeViewModel.observeOnTrip(tripId!!)
                            }

                            null -> {}
                        }
                    }
                }

                launch {
                    homeViewModel.tripStatus.collect { status ->
                        when (status) {
                            ACCEPTED, ON_WAY, ARRIVED -> {
                                markersVisibility(
                                    pickupMarkerVisibility = true,
                                    dropOffMarkerVisibility = false
                                )
                                addMarkers(
                                    addCarMarker = true,
                                    addPickUpMarker = true,
                                    addDropOffMarker = false
                                )
                            }

                            START_TRIP, PAY -> {
                                addMarkers(
                                    addCarMarker = true,
                                    addPickUpMarker = false,
                                    addDropOffMarker = true
                                )
//                                dropOffMarker?.isVisible = true
                                markersVisibility(
                                    pickupMarkerVisibility = false,
                                    dropOffMarkerVisibility = true
                                )

                            }
                            else->{
                                if (isBottomSheetVisible && tripStatus.isNotEmpty()) {
                                    // trip ended or cancelled from captain status becomes -> ""
                                    bottomSheetVisibility(false)
                                    flag =true
                                    clearMap()
                                    homeViewModel.onTrip() // to check if its ended or cancelled from captain
                                }
                                homeViewModel.resetMakeTrip()
                                homeViewModel.resetConfirmTrip()
                            }
                        }

                        tripStatus = status
                        if (flag && tripStatus.isNotEmpty() && tripId != null) {
                            if(searchingForCaptain) searchingForCaptain(false)
                            homeViewModel.getCaptainDetails(tripId!!.toLong())
                            setMyNavHostFragmentGraph(R.navigation.nav_trip_status)
                            binding.card.isVisible = false
                            bottomSheetVisibility(true)
                            flag = false
                        }

                    }
                }

                launch {
                    homeViewModel.cancelTrip.collect{
                        when(it){
                            is UiState.Failure ->{
                                showToast(it.message)
                            }
                            UiState.Loading ->{

                            }
                            is UiState.Success ->{
                                clearMap()
                                bottomSheetVisibility(false)
                            }
                            null -> {}
                        }
                    }
                }


                launch {
                    homeViewModel.cancelBeforeCapAccept.observe(viewLifecycleOwner){
                        when(it){
                            is UiState.Failure ->{
                                showToast(it.message)
                            }
                            UiState.Loading ->{

                            }
                            is UiState.Success ->{
                                searchingForCaptain(false)
                                clearMap()
                            }
                            null -> {}
                        }
                    }
                }
            }

        }
    }

    private fun bottomSheetVisibility(visibility: Boolean) {
        if(visibility){
            baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            isBottomSheetVisible = true
        }else{
            baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            isBottomSheetVisible = false
        }
    }

    private fun searchingForCaptain(flag: Boolean) {
        if (flag) {
            findingCaptainDialog?.show(childFragmentManager,DIALOG_TAG)
            searchingForCaptain = true
        }
        else{
            findingCaptainDialog?.dismiss()
            searchingForCaptain = false
        }
    }


    private fun markersVisibility(pickupMarkerVisibility:Boolean,dropOffMarkerVisibility:Boolean){
        dropOffMarker?.isVisible = dropOffMarkerVisibility
        pickupMarker?.isVisible = pickupMarkerVisibility
    }
    private fun addMarkers(addCarMarker:Boolean,addPickUpMarker: Boolean,addDropOffMarker:Boolean){
        this.addCarMarker = addCarMarker
        this.addPickUpMarker = addPickUpMarker
        this.addDropOffMarker = addDropOffMarker
    }
    private fun chooseFromMap(visibility: Boolean) {
        binding.apply {
            imgPickLocation.isVisible = visibility
            btnDone.isVisible = visibility
//            btnCancel.isVisible = visibility
            card.isVisible = !visibility
            isBottomSheetVisible = !visibility
            baseBottomSheetView.isVisible = !visibility

        }

    }

    private fun locationChecker() {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->

            try {
                task.getResult(ApiException::class.java)
                getMyLocation()
                onTrip()
                collect()

            } catch (exception: ApiException) {

                when (exception.statusCode) {

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {

                            val resolve = exception as ResolvableApiException
                            startIntentSenderForResult(
                                resolve.resolution.intentSender,
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null,
                                0,
                                0,
                                0,
                                null
                            )

                        } catch (e: Exception) {
                        }

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }

                }
            }

        }

    }



    private fun moveToCurrentLocation(latlng: LatLng) {

        val camPos = CameraPosition.builder().target(latlng).zoom(14f).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationChecker()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Priority.PRIORITY_HIGH_ACCURACY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        getMyLocation()
                        onTrip()
                        collect()
                    }

                    Activity.RESULT_CANCELED -> {
                        locationChecker()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLocationCallback = null
        mFusedLocationProviderClient = null
        mLocationCallback = null

        tripId?.let { homeViewModel.removeObserveOnTrip(it) }
        _binding = null
        findingCaptainDialog = null
        myNavHostFragment = null
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        //drawable generator
        var vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(
            0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight
        )
        //bitmap generator
        var bitmap: Bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        //canvas generator
        //pass bitmap in canvas constructor
        var canvas: Canvas = Canvas(bitmap)
        //pass canvas in drawable
        vectorDrawable.draw(canvas)
        //return BitmapDescriptorFactory
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    //to take control from the activity
    private fun onBachPressedSetup() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isEnabled) {
                val childFragment = myNavHostFragment?.childFragmentManager?.fragments

                val fragment = childFragment?.get(0)

                if (isBottomSheetVisible
                    && fragment?.javaClass != TripFragment::class.java
                ) {
                    bottomSheetVisibility(false)
                    clearMap()

                }
                else if (chooseFromMap) {
                    chooseFromMap = false
                    homeViewModel.chooseFromMap(false)
                    clearMap()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }


        }
    }

}