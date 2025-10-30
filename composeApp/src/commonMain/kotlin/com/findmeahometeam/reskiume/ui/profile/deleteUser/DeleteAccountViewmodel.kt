package com.findmeahometeam.reskiume.ui.profile.deleteUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteUserFromLocalDataSource: DeleteUserFromLocalDataSource

) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val authUserState: Flow<AuthUser?> = observeAuthStateFromAuthDataSource()

    fun deleteAccount(password: String) {
        deleteMyUserFromRemoteDataSource(password)
    }

    private fun retrieveUserUid(onAuthUserCollected: (userUid: String) -> Unit) {
        viewModelScope.launch {
            authUserState.collect { authUser: AuthUser? ->
                onAuthUserCollected(authUser?.uid ?: "")
            }
        }
    }

    private fun deleteMyUserFromRemoteDataSource(password: String) {
        retrieveUserUid { userUid: String ->

            if (userUid.isBlank()) return@retrieveUserUid

            _state.value = UiState.Loading
            deleteUserFromRemoteDataSource(userUid) { result: DatabaseResult ->
                if (result is DatabaseResult.Error) {
                    _state.value = UiState.Error(result.message)
                    Log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromRemoteDataSource: ${result.message}"
                    )
                } else {

                    // TODO delete user foster homes, events and non-human animals first

                    viewModelScope.launch {
                        val user: User? = getUserFromLocalDataSource(userUid)
                        if (user?.image.isNullOrBlank()) {
                            deleteMyUserFromAuthDataSource(userUid, password)
                            return@launch
                        }
                        deleteImageFromRemoteDataSource(
                            userUid,
                            Paths.USERS
                        ) { imageDeleted: Boolean ->
                            if (!imageDeleted) {
                                Log.e(
                                    "DeleteAccountViewmodel",
                                    "deleteMyUserFromRemoteDataSource: Error deleting user image from remote data source"
                                )
                            }
                            deleteMyUserFromAuthDataSource(userUid, password)
                        }
                    }
                }
            }
        }
    }

    private fun deleteMyUserFromAuthDataSource(userUid: String, password: String) {
        viewModelScope.launch {
            deleteUserFromAuthDataSource(password) { uid: String, errorMessage: String ->
                if (errorMessage.isNotBlank()) {
                    val errorMessageFromAuthDataSource =
                        "and from deleteMyUserFromAuthDataSource: $errorMessage"
                    deleteMyUserFromLocalDataSource(userUid, errorMessageFromAuthDataSource)
                } else {
                    deleteMyUserFromLocalDataSource(userUid)
                }
            }
        }
    }


    private fun deleteMyUserFromLocalDataSource(
        deletedUid: String,
        errorMessageFromAuthDataSource: String = ""
    ) {
        viewModelScope.launch {
            deleteUserFromLocalDataSource(deletedUid) { rowsDeleted: Int ->
                if (rowsDeleted == 0) {
                    _state.value = UiState.Error()
                    Log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromLocalDataSource: Error deleting user from local data source $errorMessageFromAuthDataSource"
                    )
                } else {
                    _state.value = UiState.Success
                    Log.d(
                        "DeleteAccountViewmodel",
                        "User deleted successfully"
                    )
                }
            }
        }
    }
}
