package com.israa.atmodrive.auth.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.israa.atmodrive.R
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.domain.model.CheckCode
import com.israa.atmodrive.auth.presentation.viewmodels.LoginViewModel
import com.israa.atmodrive.auth.presentation.viewmodels.VerifyViewModel
import com.israa.atmodrive.databinding.FragmentVerifyBinding
import com.israa.atmodrive.home.HomeActivity
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint

class VerifyFragment : Fragment() {

    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<VerifyFragmentArgs>()
    private val verifyViewModel: VerifyViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private var mobile: String? = null
    private var name: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        startCounter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mobile = args.mobileNumber
        name = args.name?.let{
            it.split(" ")[0]
        }
        determineUser(name)
        enableLoginButton()
        onClick()
        observe()
        navigate(mobile!!)

    }

    private fun determineUser(name: String?) {
        binding.apply {
            var firstIndex = -1
            var lastIndex = -1
            var title = ""
            if (name != null){
                title = getString(R.string.user_message, name, mobile)
                val spannable = SpannableString(title)
                firstIndex = title.indexOf(" ")
                lastIndex = title.indexOf(".")
                spannable.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.main_color,null)),
                    firstIndex,
                    lastIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                firstIndex = title.indexOf("+")
                spannable.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.main_color,null)),
                    firstIndex,
                    title.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                tilte.text = spannable
                btnLogin.text = getString(R.string.login)
            }
            else {
                title = getString(R.string.new_user_message, mobile)
                val spannable = SpannableString(title)
                firstIndex = title.indexOf("+")
                lastIndex = firstIndex+13
                spannable.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.main_color,null)),
                    firstIndex,
                    lastIndex,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                btnLogin.text = getString(R.string.continue_)
                 tilte.text = spannable
            }
        }
    }

    private fun navigate(mobile: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            verifyViewModel.loginEventChannel.collect { event ->
                when (event) {
                    is VerifyViewModel.VerifyEvents.NavigateToCreateAccountFragment -> {
                        //navigate to create account fragment
                        findNavController()
                            .navigate(
                                VerifyFragmentDirections.actionVerifyFragmentToCreateAccountFragment(
                                    mobile
                                )
                            )
                    }

                    is VerifyViewModel.VerifyEvents.NavigateToHomeFragment -> {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun observe() {

        verifyViewModel.checkCodeData.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    val data = it.data as CheckCode
                    verifyViewModel.isNew(data.isNew)
                    Progressbar.dismiss()
                }

                is UiState.Failure -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    Progressbar.dismiss()
                }

                is UiState.Loading ->
                    Progressbar.show(requireActivity())

                else -> {}
            }

        }

    }

    private fun onClick() {

        binding.apply {
            pinview.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    enableLoginButton()
                }

            })
            btnLogin.setOnClickListener {
                val verificationCode = pinview.text.toString()
                val deviceToken = "device_token:${mobile}"
                verifyViewModel.checkCode(mobile!!, verificationCode, deviceToken)

            }
            txtResend.setOnClickListener {
                loginViewModel.sendCode(mobile!!)
                observeOnNewCode()
            }
        }
    }

    private fun enableLoginButton() {
        binding.apply {
            btnLogin.isEnabled = pinview.text.toString().length == 4
        }
    }

    private fun startCounter() {

        Handler(Looper.myLooper()!!).postDelayed({
            binding.apply {
                txtResend.isEnabled = false
                txtCounter.visibility = VISIBLE
                txtResend.setTextColor(resources.getColor(R.color.hint_color,null))
                object : CountDownTimer(120000, 100) {
                    override fun onTick(millisUntilFinished: Long) {
                        val minutes = "${millisUntilFinished / 60000}"
                        var seconds = "${millisUntilFinished % 60000 / 1000}"
                        if (seconds.toInt() < 10)
                            seconds = "0$seconds"

                        txtCounter.text = getString(R.string.counter, minutes, seconds)
                    }

                    override fun onFinish() {
                        txtResend.isEnabled = true
                        txtResend.setTextColor(resources.getColor(R.color.main_color,null))
                        txtCounter.visibility = GONE
                    }

                }.start()
            }
        }, 500)

    }


    private fun observeOnNewCode() {
        loginViewModel.sendCodeData.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> Progressbar.show(requireActivity())
                is UiState.Success -> {
                    val data = it.data as SendCodeResponse
                    Progressbar.dismiss()
                    startCounter()
                    Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()

                }

                is UiState.Failure -> {
                    reset()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Progressbar.dismiss()
                }

                else -> Unit
            }

        }
    }


    private fun reset() {
        binding.pinview.text = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mobile = null
    }


}