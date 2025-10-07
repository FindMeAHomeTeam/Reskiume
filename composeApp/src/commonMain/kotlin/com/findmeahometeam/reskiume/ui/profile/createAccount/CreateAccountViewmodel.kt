package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateAccountViewmodel(
    private val createUserWithEmailAndPassword: CreateUserWithEmailAndPassword

) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    sealed class UiState {
        object Idle: UiState()
        object Loading: UiState()
        object Success: UiState()
        data class Error(val message: String): UiState()
    }

    fun createUserUsingEmailAndPwd(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val authResult = createUserWithEmailAndPassword(email, password)
            when(authResult) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                }
                is AuthResult.Success -> {
                    _state.value = UiState.Success
                    saveUser(authResult)
                }
            }
        }
    }

    private fun saveUser(authResult: AuthResult) {
        //TODO
    }


}