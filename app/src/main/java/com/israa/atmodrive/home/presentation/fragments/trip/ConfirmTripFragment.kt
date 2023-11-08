package com.israa.atmodrive.home.presentation.fragments.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.israa.atmodrive.R
import com.israa.atmodrive.databinding.FragmentRequestTripBinding
import com.israa.atmodrive.home.data.models.MakeTripResponse
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConfirmTripFragment : Fragment() {

    private  var _binding: FragmentRequestTripBinding?=null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRequestTripBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        onClick()
    }

    private fun onClick() {
        binding.btnRequestTrip.setOnClickListener {
            homeViewModel.confirmTrip(requireContext())
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    homeViewModel.makeTrip.collect{ result ->
                        when(result){
                            is UiState.Failure ->{
                                Progressbar.dismiss()
                                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                            }
                            UiState.Loading ->
                                Progressbar.show(requireActivity())
                            is UiState.Success ->{
                                Progressbar.dismiss()
                                setTripInfo(result.data as MakeTripResponse)
                            }
                            null ->{

                            }
                        }

                    }
                }

            }
        }
    }

    private fun setTripInfo(data: MakeTripResponse) {
        binding.apply {
            "${data.data!!.estimate_cost }${resources.getString(R.string.egp)}".also { txtTripPrice.text = it }
            "${data.data.estimate_time }${resources.getString(R.string.minutes_way_from_you)}".also { txtTime.text = it }
            txtVehicleClassName.text = data.data.vehicle_class_name
            //Glide.with(this@RequestTripFragment).load(data.data.vehicle_class_icon).into(imgVehicleClassIcon)
        }
    }
}