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
class CreateAccountViewModel @Inject constructor(private val useCase: AuthUseCase):ViewModel() {

    private var _registerPassenger = SingleLiveEvent<UiState?>()
    val registerPassenger get() = _registerPassenger
    fun registerPassenger(
        fullName: String,
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        email: String?
    ) {
        viewModelScope.launch {
            _registerPassenger.value = UiState.Loading
            when(val data =useCase.registerPassenger(fullName, mobile, avatar, deviceToken, deviceId, deviceType,email)){
                is ResponseState.Success -> _registerPassenger.postValue(UiState.Success(data.data))
                is ResponseState.Failure -> _registerPassenger.postValue(UiState.Failure(data.error))
            }
        }

    }

}