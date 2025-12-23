package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateAccountViewmodel(
    private val createUserWithEmailAndPasswordInAuthDataSource: CreateUserWithEmailAndPasswordInAuthDataSource,
    private val insertUserInRemoteDataSource: InsertUserInRemoteDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val insertUserInLocalDataSource: InsertUserInLocalDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val log: Log
) : ViewModel() {
    private var _state: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    fun createUserUsingEmailAndPwd(
        user: User,
        password: String
    ) {
        if (user.email == null) return

        viewModelScope.launch {
            _state.value = UiState.Loading()
            val authResult = createUserWithEmailAndPasswordInAuthDataSource(user.email, password)
            when (authResult) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                    log.e(
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
            extraId = "",
            section = Section.USERS,
            imageUri = user.image
        ) { imageDownloadUri: String ->
            val userWithPossibleImageDownloadUri: User = if (imageDownloadUri.isBlank()) {
                log.d(
                    "CreateAccountViewmodel",
                    "saveUserToRemoteSource: Download URI is blank"
                )
                user
            } else {
                log.d(
                    "CreateAccountViewmodel",
                    "saveUserToRemoteSource: Download URI saved successfully"
                )
                user.copy(image = imageDownloadUri)
            }
            viewModelScope.launch {
                insertUserInRemoteDataSource(userWithPossibleImageDownloadUri) { databaseResult ->
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
                userUid = userUid,
                extraId = "",
                section = Section.USERS,
                currentUserImage = currentUserImage
            ) { imageDeleted: Boolean ->
                if (!imageDeleted) {
                    log.e(
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
                    log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: $errorMessageFromDataSource"
                    )
                } else {
                    _state.value = UiState.Error("$errorMessageFromDataSource - $errorMessage")
                    log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: $errorMessageFromDataSource - $errorMessage"
                    )
                }
            }
        }
    }

    private fun saveUserToLocalSource(user: User, password: String, remoteImageUri: String) {
        viewModelScope.launch {
            insertUserInLocalDataSource(user) { rowId ->
                if (rowId > 0) {
                    log.d(
                        "CreateAccountViewmodel",
                        "User created successfully"
                    )
                    saveUserCacheLocally(user.uid)
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

    @OptIn(ExperimentalTime::class)
    private fun saveUserCacheLocally(uid: String) {
        viewModelScope.launch {
            insertCacheInLocalRepository(
                LocalCache(
                    uid = uid,
                    savedBy = uid,
                    section = Section.USERS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId ->
                if (rowId > 0) {
                    _state.value = UiState.Success(Unit)
                    log.d(
                        "CreateAccountViewmodel",
                        "User $uid cache added to local repository"
                    )
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "Error adding user $uid cache to local repository"
                    )
                }
            }
        }
    }
}
