package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateAccountViewmodel(
    private val createUserWithEmailAndPasswordFromAuthDataSource: CreateUserWithEmailAndPasswordFromAuthDataSource,
    private val insertUserToRemoteDataSource: InsertUserToRemoteDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertUserToLocalDataSource: InsertUserToLocalDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource
) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun createUserUsingEmailAndPwd(
        user: User,
        password: String
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val authResult = createUserWithEmailAndPasswordFromAuthDataSource(user.email, password)
            when (authResult) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                    Log.e(
                        "CreateAccountViewmodel",
                        "createUserUsingEmailAndPwd: ${authResult.message}"
                    )
                }

                is AuthResult.Success -> {
                    saveUserToRemoteSource(user.copy(uid = authResult.user.uid), password)
                }
            }
        }
    }

    private fun saveUserToRemoteSource(user: User, password: String) {
        uploadImageToRemoteDataSource(
            userUid = user.uid,
            imageType = Paths.USERS,
            imageUri = user.image
        ) { imageDownloadUri: String ->
            val userWithPossibleImageDownloadUri: User = if (imageDownloadUri.isBlank()) {
                Log.d(
                    "CreateAccountViewmodel",
                    "saveUserToRemoteSource: Download URI is blank"
                )
                user
            } else {
                Log.d(
                    "CreateAccountViewmodel",
                    "saveUserToRemoteSource: Download URI saved successfully"
                )
                user.copy(image = imageDownloadUri)
            }
            viewModelScope.launch {
                insertUserToRemoteDataSource(userWithPossibleImageDownloadUri) { databaseResult ->
                    when (databaseResult) {
                        is DatabaseResult.Error -> {
                            removeImageFromRemoteDataSource(
                                userUid = user.uid,
                                currentUserImage = imageDownloadUri,
                                password = password,
                                errorMessage = databaseResult.message
                            )
                        }

                        is DatabaseResult.Success -> {
                            saveUserToLocalSource(user, password, imageDownloadUri)
                        }
                    }
                }
            }
        }
    }

    private fun removeImageFromRemoteDataSource(
        userUid: String,
        currentUserImage: String,
        password: String,
        errorMessage: String
    ) {
        viewModelScope.launch {
            deleteImageFromRemoteDataSource(
                userUid,
                Paths.USERS,
                currentUserImage
            ) { imageDeleted: Boolean ->
                if (!imageDeleted) {
                    Log.e(
                        "DeleteAccountViewmodel",
                        "deleteImageFromRemoteDataSource: Error deleting user image from remote data source"
                    )
                }
                deleteAccountFromAuthDataSource(password, errorMessage)
            }
        }
    }

    private fun deleteAccountFromAuthDataSource(
        password: String,
        errorMessageFromDataSource: String
    ) {
        viewModelScope.launch {
            deleteUserFromAuthDataSource(password) { errorMessage: String ->
                if (errorMessage.isBlank()) {
                    _state.value = UiState.Error(errorMessageFromDataSource)
                    Log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: $errorMessageFromDataSource"
                    )
                } else {
                    _state.value = UiState.Error("$errorMessageFromDataSource - $errorMessage")
                    Log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: $errorMessageFromDataSource - $errorMessage"
                    )
                }
            }
        }
    }

    private fun saveUserToLocalSource(user: User, password: String, remoteImageUri: String) {
        viewModelScope.launch {
            insertUserToLocalDataSource(user) { rowId ->
                if (rowId > 0) {
                    _state.value = UiState.Success
                    Log.d(
                        "CreateAccountViewmodel",
                        "User created successfully"
                    )
                } else {
                    deleteAccountFromRemoteDataSource(
                        user.uid,
                        remoteImageUri,
                        password
                    )
                }
            }
        }
    }

    private fun deleteAccountFromRemoteDataSource(
        uid: String,
        remoteImageUri: String,
        password: String,
    ) {
        val errorMessageFromLocalDataSource = "Error saving the user to local source"

        viewModelScope.launch {
            deleteUserFromRemoteDataSource(uid) { databaseResult ->
                when (databaseResult) {
                    is DatabaseResult.Error -> {
                        removeImageFromRemoteDataSource(
                            userUid = uid,
                            currentUserImage = remoteImageUri,
                            password = password,
                            errorMessage = "$errorMessageFromLocalDataSource - ${databaseResult.message}"
                        )
                    }

                    is DatabaseResult.Success -> {
                        removeImageFromRemoteDataSource(
                            userUid = uid,
                            currentUserImage = remoteImageUri,
                            password = password,
                            errorMessage = errorMessageFromLocalDataSource
                        )
                    }
                }
            }
        }
    }
}
