package com.israa.atmodrive.auth.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.israa.atmodrive.auth.presentation.viewmodels.LoginViewModel
import com.israa.atmodrive.databinding.FragmentLoginBinding
import com.israa.atmodrive.utils.UiState
import com.israa.atmodrive.utils.Progressbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var mobile: String? = null

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        onObserve()

    }


    private fun onObserve() {
            loginViewModel.sendCodeData.observe(viewLifecycleOwner){
                when (it) {
                    is UiState.Success -> {
                        mobile?.let { navigate(it) }
                        Progressbar.dismiss()
                    }

                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Loading ->
                        Progressbar.show(requireActivity())

                    else -> Unit

            }
        }
    }

    private fun navigate(mobile: String) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToVerifyFragment(
                mobile
            )
        )
    }


    private fun onClick() {
        binding.apply {

            btnContinue.setOnClickListener {
                val mob = editTextPhone.text.toString()
                if (countryCodeHolder.selectedCountryCode.toString() == "20") {
                    loginViewModel.sendCode("0${mob}")
                    mobile = "0${mob}"

                }
                else{
                    Toast.makeText(requireContext(), "Check country code", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mobile = null
    }


}
