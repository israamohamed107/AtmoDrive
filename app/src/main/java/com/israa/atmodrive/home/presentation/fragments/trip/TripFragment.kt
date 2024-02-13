package com.israa.atmodrive.home.presentation.fragments.trip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.israa.atmodrive.R
import com.israa.atmodrive.databinding.FragmentTripBinding
import com.israa.atmodrive.home.data.models.CaptainData
import com.israa.atmodrive.home.data.models.CaptainDetailsResponse
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.ACCEPTED
import com.israa.atmodrive.utils.ARRIVED
import com.israa.atmodrive.utils.ON_WAY
import com.israa.atmodrive.utils.PAY
import com.israa.atmodrive.utils.START_TRIP
import com.israa.atmodrive.utils.UiState
import com.israa.atmodrive.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripFragment : Fragment() {


    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var captainData: CaptainData
    private var tripId:Long? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableCancelButton(true)
        collect()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            btnCancel.setOnClickListener {
                if(tripId != null){
                    homeViewModel.cancelTrip(tripId!!)
                }
            }
            imgDial.setOnClickListener {
                checkCallPermission()
            }
        }
    }

    private fun collect() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted{

                launch {
                    homeViewModel.tripID.collect{
                        when(it){
                            is UiState.Failure -> {
                            }
                            UiState.Loading -> {

                            }
                            is UiState.Success ->{
                                tripId = it.data as Long
                            }
                            null ->{}
                        }
                    }
                }
                launch {
                    homeViewModel.tripStatus.collect { status ->
                        when (status) {

                            ACCEPTED->{
                                binding.apply {
                                    btnCancel.isEnabled = true
                                    btnCancel.alpha = 1f
                                }
                                setTitle(getString(R.string.captain_accept_yor_trip),R.color.orange)

                            }
                            ON_WAY -> {
                                setTitle(getString(R.string.captain_is_on_the_way_to_you),R.color.orange)
                                binding.apply {
                                    btnCancel.isEnabled = true
                                    btnCancel.alpha = 1f
                                }
                            }

                            ARRIVED -> {
                                setTitle(getString(R.string.captain_is_arrived_to_pickup),R.color.green)
                                binding.apply {
                                    btnCancel.isEnabled = true
                                    btnCancel.alpha = 1f
                                }
                            }

                            START_TRIP -> {
                                setTitle(getString(R.string.trip_running), R.color.orange)
                                binding.apply {
                                    btnCancel.isEnabled = false
                                    btnCancel.alpha = 0.5f
                                }
                            }

                            PAY -> {
                                binding.apply {
                                    btnCancel.isEnabled = false
                                    btnCancel.alpha = 0.5f
                                }
                                setTitle( getString(R.string.you_are_arrived_to_drop_off),R.color.green)

                            }
                            else ->{

                                binding.apply {
                                    btnCancel.isEnabled = true
                                    btnCancel.alpha = 1f
                                }
                            }
                        }

                    }
                }


                launch {
                    homeViewModel.captainDetails.collect { captainDetails ->
                        when (captainDetails) {
                            is UiState.Failure -> {
                                showToast(captainDetails.message)
                            }

                            UiState.Loading -> {

                            }

                            is UiState.Success -> {
                                val data = captainDetails.data as CaptainDetailsResponse
                                captainData = data.data!!
                                setCaptainDetails(data)
                            }

                            null -> {}
                        }

                    }
                }


                launch {
                    homeViewModel.cancelTrip.collect{
                        when(it){
                            is UiState.Failure -> {
//                                enableCancelButton(true)
                                showToast(it.message)
                            }
                            UiState.Loading -> {
//                                enableCancelButton(false)
                            }
                            is UiState.Success ->{
//                                enableCancelButton(true)
                            }
                            null ->{}
                        }
                    }
                }
            }


    }

    private fun enableCancelButton(isEnabled:Boolean) {
        binding.apply {
            btnCancel.isEnabled = isEnabled
            btnCancel.alpha = if(isEnabled) {
                1f
            } else{
                .5f
            }
        }
    }

    private fun setTitle(status:String,color:Int) {
        binding.apply {
            txtTripStatus.text = status
            txtTripStatus.setBackgroundColor(
                resources.getColor(color, null)
            )
        }
    }

    private fun callCaptain() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:${captainData.mobile}")
        startActivity(callIntent)
    }

    private fun checkCallPermission() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(arrayOf(
                    Manifest.permission.CALL_PHONE
                ), 5
            )
        }
        else
            callCaptain()
    }

    private fun setCaptainDetails(captain: CaptainDetailsResponse) {
        binding.apply {
            captain.data?.let {
                txtCaptainName.text = it.full_name
                txtCarBrand.text = it.vehicle
                txtCarModel.text = it.vehicle_model
                txtTripPrice.text = it.estimate_cost.toString()
                txtCarNumber.text = it.vehicle_registration_plate
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 5 && permissions.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            callCaptain()
        }
    }

}