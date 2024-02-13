package com.israa.atmodrive.auth.presentation.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.israa.atmodrive.auth.data.model.SendCodeResponse
import com.israa.atmodrive.auth.presentation.viewmodels.LoginViewModel
import com.israa.atmodrive.databinding.FragmentLoginBinding
import com.israa.atmodrive.utils.Progressbar
import com.israa.atmodrive.utils.UiState
import com.israa.atmodrive.utils.showToast
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
        enableContinueButton()
        onClick()
        onObserve()

    }


    private fun onObserve() {
            loginViewModel.sendCodeData.observe(viewLifecycleOwner){ response->
                when (response) {
                    is UiState.Success -> {
                        binding.editTextPhone.text = null
                        mobile?.let { navigate(it,(response.data as SendCodeResponse).data?.full_name )}
                        Progressbar.dismiss()
                    }

                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        showToast(response.message)
                    }

                    is UiState.Loading ->
                        Progressbar.show(requireActivity())

                    else -> Unit

            }
        }
    }

    private fun navigate(mobile: String,name:String?) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToVerifyFragment(
                mobile,
                name
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
                else {
                    showToast("Check country code")
                }
            }

            editTextPhone.setOnLongClickListener {
                val clipboard =
                    (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE)) as? ClipboardManager
                var pasteText = ""
                clipboard?.let {
                    if (it.hasPrimaryClip()) {
                        val item = it.primaryClip!!.getItemAt(0)
                        pasteText = item.text.toString()
                    }
                    if (pasteText.isNotEmpty()) {
                        pasteText = pasteText.replace("+20", "")
                            .replace(" ", "")
                        if(pasteText[0] == '0'){
                            pasteText = pasteText.replaceFirst("0","")
                        }
                        if (pasteText.length != 10) {
                            pasteText = ""
                        }
                        val clip = ClipData.newPlainText("simple text", pasteText)
                        it.setPrimaryClip(clip)
                    }
                }
                return@setOnLongClickListener true
            }

            editTextPhone.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(arg0: Editable) {
                    enableContinueButton()
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
        }

    }

    private fun enableContinueButton() {
        binding.apply {
            btnContinue.isEnabled = editTextPhone.text.toString().length == 10
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mobile = null
    }


}
