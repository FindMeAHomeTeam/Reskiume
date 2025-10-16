package com.findmeahometeam.reskiume.ui.profile.deleteUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewmodel(
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,

) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    sealed class UiState {
        object Idle: UiState()
        object Loading: UiState()
        object Success: UiState()
        data class Error(val message: String): UiState()
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            deleteUserFromAuthDataSource(password) { uid: String, errorMessage: String ->
                if (errorMessage.isBlank()) {
                    removeUserFromDatabase(deletedUid = uid)
                    _state.value = UiState.Success
                } else {
                    _state.value = UiState.Error(errorMessage)
                }
            }
        }
    }

    private fun removeUserFromDatabase(deletedUid: String) {
        //TODO
    }
}
