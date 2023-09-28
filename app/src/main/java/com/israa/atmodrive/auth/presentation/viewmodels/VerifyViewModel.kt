package com.israa.atmodrive.auth.presentation.viewmodels

import SingleLiveEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(private val useCase: AuthUseCase):ViewModel() {


    private var _checkCodeData =
        SingleLiveEvent<UiState?>()
    val checkCodeData get() = _checkCodeData

    private val _verifyEventChannel = Channel<VerifyEvents>()
    val loginEventChannel = _verifyEventChannel.receiveAsFlow()

    fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ) {
        viewModelScope.launch {
            _checkCodeData.value = UiState.Loading
            when (val data = useCase.checkCode(mobile, verificationCode, deviceToken)) {
                is ResponseState.Success -> _checkCodeData.postValue(UiState.Success(data.data))
                is ResponseState.Failure -> _checkCodeData.postValue(UiState.Failure(data.error))
            }
        }
    }

    fun isNew(isNew: Boolean?) {
        if (isNew == true) {
            viewModelScope.launch {
                _verifyEventChannel.send(VerifyEvents.NavigateToCreateAccountFragment)
            }
        }
        else{
            viewModelScope.launch {
                _verifyEventChannel.send(VerifyEvents.NavigateToHomeFragment)
            }
        }
    }


    sealed class VerifyEvents{
        object NavigateToCreateAccountFragment : VerifyEvents()
        object NavigateToHomeFragment : VerifyEvents()
    }
}