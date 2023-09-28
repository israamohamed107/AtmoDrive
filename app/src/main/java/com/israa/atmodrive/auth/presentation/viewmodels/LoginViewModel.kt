package com.israa.atmodrive.auth.presentation.viewmodels

import SingleLiveEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import com.israa.atmodrive.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: IAuthUseCase):ViewModel() {

    private var _sendCodeData = SingleLiveEvent<UiState?>()
    val sendCodeData get() = _sendCodeData


    fun sendCode(mobile: String) {
        viewModelScope.launch {
            _sendCodeData.value = UiState.Loading
            when (val data = useCase.sendCode(mobile)) {
                is ResponseState.Success -> _sendCodeData.postValue(UiState.Success(data.data))
                is ResponseState.Failure -> _sendCodeData.postValue(UiState.Failure(data.error))
            }
        }
    }




}