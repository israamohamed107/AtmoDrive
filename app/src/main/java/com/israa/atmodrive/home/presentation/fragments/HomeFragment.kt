package com.israa.atmodrive.home.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.israa.atmodrive.R
import com.israa.atmodrive.databinding.FragmentHomeBinding
import com.israa.atmodrive.home.data.models.TripModel
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.ACCEPT_TRIP
import com.israa.atmodrive.utils.DROP_OFF
import com.israa.atmodrive.utils.PICK_UP
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.START_TRIP
import com.israa.atmodrive.utils.TRIPS
import com.israa.atmodrive.utils.UiState
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

    private lateinit var pickUpLocationAddress: String

    private var pickUpLocation: LatLng? = null
    private var dropOffLocation: LatLng? = null

    private lateinit var myNavHostFragment: NavHostFragment
    private lateinit var baseBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var baseBottomSheetView: ConstraintLayout

    private var chooseFromMap: Boolean = false
    private var input: String = ""
    private var isBottomSheetVisible = false
    
    private val homeViewModel: HomeViewModel by activityViewModels()


    private lateinit var valueEventListener:ValueEventListener
    @Inject
    lateinit var ref :DatabaseReference

    private var tripStatus:String? = null

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

        onTrip()
        initLocation()
        initViews()
        onClick()
        collect()
        onBachPressedSetup()


    }

    private fun onTrip() {
        homeViewModel.onTrip()
    }
    private fun observeOnTrip(tripId: String)
    {
        Progressbar.show(requireActivity())
        valueEventListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    Progressbar.dismiss()
                    val trip = snapshot.getValue(TripModel::class.java)
                    Toast.makeText(requireContext(), trip!!.status, Toast.LENGTH_SHORT).show()

                    if (trip.status != tripStatus){


                        if(trip.status == ACCEPT_TRIP){
                            tripStatus = trip.status
                            homeViewModel.setTripStatus(tripStatus!!)
                            dropOffMarker?.isVisible = false
                            addCarMarkerOnMap(LatLng(trip.lat!!.toDouble(),trip.lng!!.toDouble()))

                        }else if (trip.status == START_TRIP){
                            tripStatus = START_TRIP
                            homeViewModel.setTripStatus(tripStatus!!)
                            pickupMarker?.remove()
                            dropOffMarker?.isVisible = true
                            addCarMarkerOnMap(LatLng(trip.lat!!.toDouble(),trip.lng!!.toDouble()))
                        }

                        setMyNavHostFragmentGraph(R.navigation.nav_trip_status,.40)
                        baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        baseBottomSheetBehavior.isDraggable = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.details, Toast.LENGTH_SHORT).show()
            }

        }
        ref.child(TRIPS).child(tripId).addValueEventListener(valueEventListener)
    }

    private fun removeObserveOnTrip(tripId:String){
        ref.child(TRIPS).child(tripId).removeEventListener(valueEventListener)
    }
    private fun initLocation() {

        mFusedLocationProviderClient.let {
            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        }

        mLocationRequest.let {
            mLocationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15 * 60 * 1000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(2000) //FastestInterval
                    .setMaxUpdateDelayMillis(2000) //locationMaxWaitTime
                    .setMinUpdateDistanceMeters(5f).build()

//            mLocationRequest = LocationRequest.create()
//                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//                .setInterval(2000)
//                .setFastestInterval(2000)
//                .setSmallestDisplacement(5f)

        }

    }

    private fun initViews() {
        baseBottomSheetView = requireActivity().findViewById(R.id.base_bottom_sheet)
        baseBottomSheetBehavior = BottomSheetBehavior.from(baseBottomSheetView)

    }

    private fun onClick() {
        binding.apply {
            txtDestination.setOnClickListener {
                setMyNavHostFragmentGraph(R.navigation.nav_make_trip, .70)
               isBottomSheetVisible = true
            }

            btnDone.setOnClickListener {
                homeViewModel.chooseFromMap(false)
                if (pickUpLocation != null && dropOffLocation != null) {
                    homeViewModel.makeTrip()
                }
            }

            btnCancel.setOnClickListener {
                homeViewModel.chooseFromMap(false)
                clearMap()
                baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
            imgGetCurrentLocation.setOnClickListener {
                getMyLocation()
            }
        }


    }

    private fun cameraBounds(pickUpLocation: LatLng, dropOffLocation: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(pickUpLocation)
        builder.include(dropOffLocation)
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
        baseBottomSheetBehavior.isDraggable = true
        homeViewModel.setDropOffLocation(null)
        dropOffMarker = null
        pickupMarker = null
        getMyLocation()
        mMap.clear()
    }

    private fun setMyNavHostFragmentGraph(navGraphId: Int, heightPercent: Double) {
        val inflater = myNavHostFragment.navController.navInflater
        val graph = inflater.inflate(navGraphId)
        myNavHostFragment.navController.graph = graph
        baseBottomSheetBehavior.maxHeight =
            (((requireActivity().resources.displayMetrics).heightPixels) * heightPercent).toInt()
        baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun addDropOffMarkerOnMap(dropOffLocation: LatLng) {
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

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(captainLocation, 14f))

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
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
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

            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else getMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {

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
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    homeViewModel.passengerOnTrip.collect{ status ->
                        when(status){
                            is UiState.Failure ->{
                                Progressbar.dismiss()
                                Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                            }
                            UiState.Loading ->{
                                Progressbar.show(requireActivity())
                            }
                            is UiState.Success ->{
                                Progressbar.dismiss()
                                Toast.makeText(requireContext(),status.data.toString() , Toast.LENGTH_SHORT).show()
                            }
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
                            }else{
                                Toast.makeText(requireContext(), "PickUp Null", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                launch {
                    homeViewModel.dropOffLocation.collect {
                        if(it!=null)
                                dropOffLocation = it
                        else
                            Toast.makeText(requireContext(), "Drop off Null", Toast.LENGTH_SHORT).show()

                    }
                }

                launch {
                    homeViewModel.chooseFromMap.collect {
                            chooseFromMap = it
                            chooseFromMap(it)
                        if(it)
                            pickLocation()

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
                                    setMyNavHostFragmentGraph(R.navigation.nav_request_trip, .50)
                                    baseBottomSheetBehavior.apply {
                                        state = BottomSheetBehavior.STATE_EXPANDED
                                        isDraggable = false
                                    }
                                   isBottomSheetVisible = true
                                }

                                else -> {

                                }
                            }
                        }
                }

                launch {
                    homeViewModel.confirmTrip.collect{ status ->
                        when(status){
                            is UiState.Failure -> {
                                Progressbar.dismiss()
                                Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                                //observeOnTrip("5")
                            }
                            UiState.Loading ->
                                Progressbar.show(requireActivity())
                            is UiState.Success ->{
                                Progressbar.dismiss()
                                Toast.makeText(requireContext(), "Confirm trip Success", Toast.LENGTH_SHORT).show()
                                baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                            null -> {}
                        }
                    }
                }
            }

        }
    }



    private fun chooseFromMap(visibility: Boolean) {
        binding.apply {
            imgPickLocation.isVisible = visibility
            btnDone.isVisible = visibility
            btnCancel.isVisible = visibility
            card.isVisible = !visibility
            imgGetCurrentLocation.isVisible = !visibility
           isBottomSheetVisible = !visibility
            baseBottomSheetView.isVisible = !visibility

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
            getMyLocation()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mLocationCallback = null
        mFusedLocationProviderClient = null
        mLocationCallback = null

        removeObserveOnTrip("5")
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
                val childFragment = myNavHostFragment.childFragmentManager.fragments

                if (childFragment.size != 0 &&isBottomSheetVisible) {
                    baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                   isBottomSheetVisible = false
                    clearMap()

                } else if (chooseFromMap) {
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