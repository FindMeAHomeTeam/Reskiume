package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Log
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
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
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource
) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

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
            val userWithImageDownloadUri: User = user.copy(image = imageDownloadUri).also {
                if(it.image.isBlank()) {
                    Log.e(
                        "CreateAccountViewmodel",
                        "saveUserToRemoteSource: Image download URI is blank"
                    )
                }
            }
            viewModelScope.launch {
                insertUserToRemoteDataSource(userWithImageDownloadUri) { databaseResult ->
                    when (databaseResult) {
                        is DatabaseResult.Error -> {
                            deleteAccountFromAuthDataSource(password, databaseResult.message)
                        }

                        is DatabaseResult.Success -> {
                            saveUserToLocalSource(user, password)
                        }
                    }
                }
            }
        }
    }

    private fun deleteAccountFromAuthDataSource(
        password: String,
        errorMessageFromDataSource: String
    ) {
        viewModelScope.launch {
            deleteUserFromAuthDataSource(password) { uid: String, errorMessage: String ->
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

    private fun saveUserToLocalSource(user: User, password: String) {
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
                        password
                    )
                }
            }
        }
    }

    private fun deleteAccountFromRemoteDataSource(
        uid: String,
        password: String,
    ) {
        val errorMessageFromLocalDataSource = "Error saving the user to local source"

        viewModelScope.launch {
            deleteUserFromRemoteDataSource(uid) { databaseResult ->
                when (databaseResult) {
                    is DatabaseResult.Error -> {
                        deleteAccountFromAuthDataSource(
                            password,
                            "$errorMessageFromLocalDataSource - ${databaseResult.message}"
                        )
                    }

                    is DatabaseResult.Success -> {
                        deleteAccountFromAuthDataSource(password, errorMessageFromLocalDataSource)
                    }
                }
            }
        }
    }
}
