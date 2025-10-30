package com.findmeahometeam.reskiume.ui.profile.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Log
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.SaveImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LoginViewmodel(
    private val signInWithEmailAndPasswordFromAuthDataSource: SignInWithEmailAndPasswordFromAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val saveImageToLocalDataSource: SaveImageToLocalDataSource,
    private val insertUserToLocalDataSource: InsertUserToLocalDataSource,
    private val modifyUserFromLocalDataSource: ModifyUserFromLocalDataSource

) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun signInUsingEmail(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val authResult = signInWithEmailAndPasswordFromAuthDataSource(email, password)
            when (authResult) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                }

                is AuthResult.Success -> {
                    updateLocalUser(authResult.user)
                }
            }
        }
    }

    private fun updateLocalUser(authUser: AuthUser) {
        viewModelScope.launch {
            val user: User? = getUserFromLocalDataSource(authUser.uid)

            if (user == null) {
                getUserFromRemoteDataSourceAndSaveItsAvatar(authUser.uid) { collectedUser: User ->
                    viewModelScope.launch {
                        insertUserToLocalDataSource(collectedUser) { rowId: Long ->
                            if (rowId > 0) {
                                Log.d(
                                    "LoginViewmodel",
                                    "Inserted user with uid ${collectedUser.uid} into local data source."
                                )
                                _state.value = UiState.Success
                            } else {
                                Log.e(
                                    "LoginViewmodel",
                                    "Failed to insert user with uid ${collectedUser.uid} into local data source."
                                )
                                _state.value = UiState.Error()
                            }
                        }
                    }
                }
            } else if (hasPassed24Hours(user.lastLogout)) {

                getUserFromRemoteDataSourceAndSaveItsAvatar(authUser.uid) { collectedUser: User ->
                    viewModelScope.launch {
                        modifyUserFromLocalDataSource(collectedUser) { rowsModified: Int ->
                            if (rowsModified > 0) {
                                Log.d(
                                    "LoginViewmodel",
                                    "Modified user with uid ${collectedUser.uid} into local data source."
                                )
                                _state.value = UiState.Success
                            } else {
                                Log.e(
                                    "LoginViewmodel",
                                    "Failed to modify user with uid ${collectedUser.uid} in local data source."
                                )
                                _state.value = UiState.Error()
                            }
                        }
                    }
                }
            } else {
                Log.d(
                    "LoginViewmodel",
                    "User with uid ${user.uid} is up-to-date in local data source."
                )
                _state.value = UiState.Success
            }
        }
    }

    private suspend fun getUserFromRemoteDataSourceAndSaveItsAvatar(
        userUid: String,
        onSavedAvatar: (collectedUser: User) -> Unit
    ) {
        getUserFromRemoteDataSource(userUid).collect { collectedUser: User? ->
            if (collectedUser == null) {
                Log.d(
                    "LoginViewmodel",
                    "Unless it is the default collectedUser value, it seems that the user $userUid was not found in the remote data source despite successful authentication."
                )
            } else if (collectedUser.image.isNotBlank()){
                saveImageToLocalDataSource(
                    userUid = collectedUser.uid,
                    imageType = Paths.USERS
                ) { localImagePath: String ->
                    onSavedAvatar(collectedUser.copy(image = localImagePath.ifBlank { collectedUser.image }))
                }
            } else {
                Log.d(
                    "LoginViewmodel",
                    "User ${collectedUser.uid} has no avatar image to save locally."
                )
                onSavedAvatar(collectedUser)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun hasPassed24Hours(savedEpochSeconds: Long): Boolean {
        val nowEpoch: Long = Clock.System.now().epochSeconds
        return (nowEpoch - savedEpochSeconds) >= 24 * 60 * 60
    }

    //TODO save foster homes, rescue events, and non-human animals related to the user
}
