package com.israa.atmodrive.home.presentation.fragments.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrive.R
import com.israa.atmodrive.databinding.FragmentRequestTripBinding
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConfirmTripFragment : Fragment() {

    private var _binding: FragmentRequestTripBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var tripInfo: MakeTripResponse
    private lateinit var pickupLocation: LatLng
    private lateinit var dropOffLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRequestTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        onClick()
    }

    private fun onClick() {
        binding.btnRequestTrip.setOnClickListener {
                homeViewModel.confirmTrip(tripInfo.data!!,pickupLocation,dropOffLocation,requireContext())
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    homeViewModel.pickUpLocation.collect { pickup ->
                        pickup?.let { pickupLocation = it }
                    }
                }
                launch {
                    homeViewModel.dropOffLocation.collect { drop ->
                        drop?.let { dropOffLocation = it }
                    }
                }
                launch {
                    homeViewModel.makeTrip.collect { result ->
                        when (result) {
                            is UiState.Failure -> {

                            }

                            UiState.Loading -> {}
                            is UiState.Success -> {
                                val data = result.data as MakeTripResponse
                                tripInfo = data
                                setTripInfo(tripInfo)
                            }

                            null -> {

                            }
                        }

                    }
                }
            }

        }

    }

    private fun setTripInfo(data: MakeTripResponse) {
        binding.apply {
            "${data.data?.estimate_cost} ${resources.getString(R.string.egp)}".also {
                txtTripPrice.text = it
            }
            "${data.data?.estimate_time} ${resources.getString(R.string.minutes_way_from_you)}".also {
                txtTime.text = it
            }
            txtVehicleClassName.text = data.data?.vehicle_class_name
            //Glide.with(this@RequestTripFragment).load(data.data.vehicle_class_icon).into(imgVehicleClassIcon)
        }
    }
}