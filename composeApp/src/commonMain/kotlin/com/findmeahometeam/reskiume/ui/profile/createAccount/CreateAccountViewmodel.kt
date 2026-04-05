package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
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

    fun saveUserChanges(
        user: User,
        password: String
    ) {
        _state.value = UiState.Loading()

        createAuthUserUsingEmailAndPwd(user, password) { updatedUser: User ->

            uploadImageToRemoteRepo(updatedUser) { updatedUserWithRemoteImage: User, imageDownloadUri: String ->

                saveUserToRemoteRepo(
                    user = updatedUserWithRemoteImage,
                    onSuccess = {

                        insertUserInLocalRepo(
                            user = updatedUser,
                            onSuccess = {

                                saveUserCacheLocally(updatedUser.uid)
                            },
                            onError = {

                                deleteAccountFromRemoteDataSource(
                                    uid = updatedUserWithRemoteImage.uid,
                                    onComplete = {
                                        deleteImageFromRemoteRepo(
                                            userUid = updatedUserWithRemoteImage.uid,
                                            currentUserImage = imageDownloadUri
                                        ) {
                                            deleteAccountFromAuthDataSource(password)
                                        }
                                    },
                                    onError = { errorMessage ->

                                        deleteImageFromRemoteRepo(
                                            userUid = updatedUserWithRemoteImage.uid,
                                            currentUserImage = imageDownloadUri
                                        ) {
                                            deleteAccountFromAuthDataSource(
                                                password = password,
                                                errorMessageFromDataSource = errorMessage
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    },
                    onError = { errorMessage ->

                        deleteImageFromRemoteRepo(
                            userUid = updatedUserWithRemoteImage.uid,
                            currentUserImage = imageDownloadUri
                        ) {
                            deleteAccountFromAuthDataSource(
                                password = password,
                                errorMessageFromDataSource = errorMessage
                            )
                        }
                    }
                )
            }
        }
    }

    private fun createAuthUserUsingEmailAndPwd(
        user: User,
        password: String,
        onSuccess: (user: User) -> Unit
    ) {
        viewModelScope.launch {

            val authResult = createUserWithEmailAndPasswordInAuthDataSource(user.email!!, password)

            if (authResult is AuthResult.Success) {
                log.d(
                    "CreateAccountViewmodel",
                    "createAuthUserUsingEmailAndPwd: auth user ${authResult.user.uid} created in the auth repository"
                )
                onSuccess(user.copy(uid = authResult.user.uid))

            } else {
                log.e(
                    "CreateAccountViewmodel",
                    "createAuthUserUsingEmailAndPwd: auth user not created in the auth repository - ${(authResult as AuthResult.Error).message}"
                )
                _state.value = UiState.Error(authResult.message)
            }
        }
    }

    private fun uploadImageToRemoteRepo(
        user: User,
        onSuccess: (user: User, imageDownloadUri: String) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = user.uid,
            extraId = "",
            section = Section.USERS,
            imageUri = user.image
        ) { imageDownloadUri: String ->

            val userWithPossibleImageDownloadUri: User = if (imageDownloadUri.isBlank()) {
                log.d(
                    "CreateAccountViewmodel",
                    "uploadImageToRemoteRepo: Download URI is blank"
                )
                user
            } else {
                log.d(
                    "CreateAccountViewmodel",
                    "uploadImageToRemoteRepo: Download URI saved successfully"
                )
                user.copy(image = imageDownloadUri)
            }
            onSuccess(userWithPossibleImageDownloadUri, imageDownloadUri)
        }
    }

    private fun saveUserToRemoteRepo(
        user: User,
        onSuccess: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        viewModelScope.launch {
            insertUserInRemoteDataSource(user) { databaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "CreateAccountViewmodel",
                        "saveUserToRemoteRepo: user ${user.uid} saved successfully in the remote repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "saveUserToRemoteRepo: failed to save the user ${user.uid} in the remote repository"
                    )
                    onError((databaseResult as DatabaseResult.Error).message)
                }
            }
        }
    }

    private fun deleteImageFromRemoteRepo(
        userUid: String,
        currentUserImage: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            deleteImageFromRemoteDataSource(
                userUid = userUid,
                extraId = "",
                section = Section.USERS,
                currentImage = currentUserImage
            ) { imageDeleted: Boolean ->

                if (imageDeleted) {

                    log.d(
                        "CreateAccountViewmodel",
                        "deleteImageFromRemoteRepo: user image deleted successfully in the remote repository"
                    )
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "deleteImageFromRemoteRepo: Error deleting the user image in the remote repository"
                    )
                }
                onComplete()
            }
        }
    }

    private fun deleteAccountFromAuthDataSource(
        password: String,
        errorMessageFromDataSource: String = ""
    ) {
        viewModelScope.launch {

            deleteUserFromAuthDataSource(password) { errorMessage: String ->
                if (errorMessage.isBlank()) {
                    log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: deleted account from the auth repository - $errorMessageFromDataSource"
                    )
                    _state.value = UiState.Error(errorMessageFromDataSource)
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "deleteAccountFromAuthDataSource: failed to delete account from the auth repository - $errorMessageFromDataSource - $errorMessage"
                    )
                    _state.value = UiState.Error("$errorMessageFromDataSource - $errorMessage")
                }
            }
        }
    }

    private fun insertUserInLocalRepo(
        user: User,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            insertUserInLocalDataSource(user) { rowId ->
                if (rowId > 0) {
                    log.d(
                        "CreateAccountViewmodel",
                        "insertUserInLocalRepo: User ${user.uid} created successfully in the local repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "insertUserInLocalRepo: failed to create the user ${user.uid} in the local repository"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteAccountFromRemoteDataSource(
        uid: String,
        onComplete: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        deleteUserFromRemoteDataSource(uid) { databaseResult ->

            if (databaseResult is DatabaseResult.Success) {
                log.d(
                    "CreateAccountViewmodel",
                    "deleteAccountFromRemoteDataSource: user $uid deleted successfully in the remote repository"
                )
                onComplete()
            } else {
                log.e(
                    "CreateAccountViewmodel",
                    "deleteAccountFromRemoteDataSource: failed to delete the user $uid in the remote repository - ${(databaseResult as DatabaseResult.Error).message}"
                )
                onError(databaseResult.message)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun saveUserCacheLocally(uid: String) {
        viewModelScope.launch {

            insertCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = uid,
                    savedBy = uid,
                    section = Section.USERS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId ->

                if (rowId > 0) {
                    _state.value = UiState.Success(Unit)
                    log.d(
                        "CreateAccountViewmodel",
                        "saveUserCacheLocally: user $uid added to the local cache in section ${Section.USERS}"
                    )
                } else {
                    log.e(
                        "CreateAccountViewmodel",
                        "saveUserCacheLocally: Error adding the user $uid to the local cache in section ${Section.USERS}"
                    )
                }
            }
        }
    }
}
