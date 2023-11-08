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
import com.israa.atmodrive.R
import com.israa.atmodrive.databinding.FragmentTripBinding
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.START_TRIP
import kotlinx.coroutines.launch

class TripFragment : Fragment() {


    private  var _binding: FragmentTripBinding?=null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var tripStatus:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTripBinding.inflate(inflater,container,false)
        return binding.root

        collect()
    }

    private fun collect(){

        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.tripStatus.collect{ status ->
                    when(status){
                        START_TRIP ->{
                            binding.apply {
                                txtTripStatus.text = getString(R.string.trip_running)
                                txtTripStatus.setBackgroundColor(resources.getColor(R.color.orange,null))
                            }
                        }
                    }

                }
            }

        }
    }

}