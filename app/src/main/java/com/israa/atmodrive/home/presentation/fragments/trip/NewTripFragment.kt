package com.israa.atmodrive.home.presentation.fragments.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.israa.atmodrive.databinding.FragmentNewTripBinding
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.DROP_OFF
import com.israa.atmodrive.utils.PICK_UP
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewTripFragment : Fragment() {

    private  var _binding: FragmentNewTripBinding?=null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewTripBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()

        onClick()

    }

    private fun onClick() {

        binding.apply {
            txtChooseFromMap.setOnClickListener {
                homeViewModel.chooseFromMap(true)
            }
            edTxtPickup.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    homeViewModel.setCurrentInput(PICK_UP)
                }
            }
            edTxtDropOff.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    homeViewModel.setCurrentInput(DROP_OFF)
                }
            }
        }

    }

    private fun observe(){
           viewLifecycleOwner.lifecycleScope.launch {
               homeViewModel.pickUpLocation
                   .flowWithLifecycle(lifecycle )
                   .collect{ currentLocation ->
                       if(currentLocation!=null){
                           binding.edTxtPickup.setText(homeViewModel.getAddress(requireContext(),currentLocation))
                       }
                   }
           }
        }

}