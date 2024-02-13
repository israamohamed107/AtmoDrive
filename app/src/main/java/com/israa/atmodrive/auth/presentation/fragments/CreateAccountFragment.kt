package com.israa.atmodrive.auth.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.israa.atmodrive.auth.presentation.viewmodels.CreateAccountViewModel
import com.israa.atmodrive.databinding.FragmentCreateAccountBinding
import com.israa.atmodrive.home.HomeActivity
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private val args by navArgs<CreateAccountFragmentArgs>()

    private var mobile: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mobile = args.mobile
        onClick()
        enableSubmitButton()
    }

    private fun onObserve() {
            createAccountViewModel.registerPassenger.observe(viewLifecycleOwner){
                when (it) {


                    is UiState.Success -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        requireActivity().finish()
                        Progressbar.dismiss()
                    }

                    is UiState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Progressbar.dismiss()
                    }
                    is UiState.Loading -> {}
                    else -> {
                        Progressbar.show(requireActivity())

                    }
                }
            }

    }

    private fun onClick() {

        binding.apply {

            editTextName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(arg0: Editable) {
                    enableSubmitButton()
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            btnSubmit.setOnClickListener {
                createAccountViewModel.registerPassenger(
                    editTextName.text.toString(),
                    mobile = mobile!!,
                    avatar = null,
                    deviceToken = "device_token:${mobile}",
                    deviceId = "device_id:Huwai",
                    deviceType = "android",
                    email = editTextEmail.text.toString()
                )

                onObserve()

            }

        }
    }

    private fun enableSubmitButton() {
        binding.apply {
            val spaces = editTextName.text.toString().filter { it == ' ' }.length
            btnSubmit.isEnabled = spaces <= 3
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mobile = null
    }

}