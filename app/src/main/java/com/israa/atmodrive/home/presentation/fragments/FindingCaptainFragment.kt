package com.israa.atmodrive.home.presentation.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.israa.atmodrive.databinding.FragmentFindingCaptainBinding
import com.israa.atmodrive.home.viewmodels.HomeViewModel
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FindingCaptainFragment : DialogFragment() {

    private var _binding: FragmentFindingCaptainBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var tripId: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFindingCaptainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.tripID.collect {
                when (it) {
                    is UiState.Failure -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        tripId = it.data as Long
                    }

                    null -> {
                        tripId = null
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            tripId?.let { homeViewModel.cancelBeforeCaptainAccept(it) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        return dialog
    }



}