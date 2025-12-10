package com.findmeahometeam.reskiume.ui.profile.modifyAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ModifyAccountViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val modifyUserEmailInAuthDataSource: ModifyUserEmailInAuthDataSource,
    private val modifyUserPasswordInAuthDataSource: ModifyUserPasswordInAuthDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val modifyUserInRemoteDataSource: ModifyUserInRemoteDataSource,
    private val modifyUserInLocalDataSource: ModifyUserInLocalDataSource,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val signOutFromAuthDataSource: SignOutFromAuthDataSource,
    private val log: Log
) : ViewModel() {

    private val authUserState: Flow<AuthUser?> = observeAuthStateInAuthDataSource()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun saveUserChanges(
        isDifferentEmail: Boolean,
        isDifferentImage: Boolean,
        user: User,
        currentPassword: String,
        newPassword: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            updateUserEmailInAuthDataSource(isDifferentEmail, currentPassword, user.email) {

                updateUserPasswordInAuthDataSource(currentPassword, newPassword) {

                    if (isDifferentImage) {
                        deleteCurrentImageInRemoteDataSource(user) {

                            deleteCurrentImageInLocalDataSource(user) {

                                uploadNewImageToRemoteDataSource(user) { userWithPossibleImageDownloadUri: User ->

                                    updateUserInRemoteDataSource(userWithPossibleImageDownloadUri) {

                                        saveUserChangesInLocalDataSource(user)
                                    }
                                }
                            }
                        }
                    } else {
                        getUserFromRemoteDataSource(user.uid).collect { collectedUser: User? ->

                            if (collectedUser == null) {
                                return@collect
                            }
                            updateUserInRemoteDataSource(user.copy(image = collectedUser.image)) {

                                saveUserChangesInLocalDataSource(user)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateUserEmailInAuthDataSource(
        isDifferentEmail: Boolean,
        password: String,
        newEmail: String?,
        onSuccess: suspend () -> Unit
    ) {

        if (!isDifferentEmail || newEmail.isNullOrBlank()) {
            onSuccess()
            return
        }
        modifyUserEmailInAuthDataSource(password, newEmail) { error ->
            if (error.isBlank()) {
                log.d(
                    "ModifyAccountViewmodel",
                    "updateUserEmailInAuthDataSource: accepted request to update user email in auth data source"
                )
                viewModelScope.launch {
                    onSuccess()
                }
            } else {
                log.e(
                    "ModifyAccountViewmodel",
                    "updateUserEmailInAuthDataSource: failed to update user email in auth data source: $error"
                )
                _uiState.value = UiState.Error(error)
            }
        }
    }

    private suspend fun updateUserPasswordInAuthDataSource(
        currentPassword: String,
        newPassword: String,
        onSuccess: suspend () -> Unit
    ) {
        if (newPassword.isBlank()) {
            onSuccess()
            return
        }
        modifyUserPasswordInAuthDataSource(currentPassword, newPassword) { error ->
            if (error.isBlank()) {
                log.d(
                    "ModifyAccountViewmodel",
                    "updateUserPasswordInAuthDataSource: User password updated successfully in auth data source"
                )
                viewModelScope.launch {
                    onSuccess()
                }
            } else {
                log.e(
                    "ModifyAccountViewmodel",
                    "updateUserPasswordInAuthDataSource: failed to update user password in auth data source: $error"
                )
                _uiState.value = UiState.Error(error)
            }
        }
    }

    private suspend fun deleteCurrentImageInRemoteDataSource(user: User, onSuccess: () -> Unit) {
        getUserFromRemoteDataSource(user.uid).collect { previousUserData: User? ->

            if (previousUserData == null) {
                return@collect
            }
            deleteImageFromRemoteDataSource(
                userUid = user.uid,
                extraId = "",
                section = Section.USERS,
                currentUserImage = previousUserData.image
            ) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyAccountViewmodel",
                        "deleteCurrentImageInRemoteDataSource: Image deleted successfully in remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyAccountViewmodel",
                        "deleteCurrentImageInRemoteDataSource: failed to delete image in remote data source"
                    )
                    _uiState.value = UiState.Error()
                }
            }
        }
    }

    private fun deleteCurrentImageInLocalDataSource(user: User, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val previousUserData: User = getUserFromLocalDataSource(user.uid)!!

            deleteImageFromLocalDataSource(currentImagePath = previousUserData.image) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyAccountViewmodel",
                        "deleteCurrentImageInLocalDataSource: Image deleted successfully in local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyAccountViewmodel",
                        "deleteCurrentImageInLocalDataSource: failed to delete image in local data source"
                    )
                    _uiState.value = UiState.Error()
                }
            }
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        user: User,
        onSuccess: suspend (userWithImageDownloadUri: User) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = user.uid,
            extraId = "",
            section = Section.USERS,
            imageUri = user.image
        ) { imageDownloadUri: String ->
            val userWithPossibleImageDownloadUri: User = if (imageDownloadUri.isBlank()) {
                log.d(
                    "ModifyAccountViewmodel",
                    "uploadNewImageToRemoteDataSource: Download URI is blank"
                )
                user
            } else {
                log.d(
                    "ModifyAccountViewmodel",
                    "uploadNewImageToRemoteDataSource: Download URI saved successfully"
                )
                user.copy(image = imageDownloadUri)
            }
            viewModelScope.launch {
                onSuccess(userWithPossibleImageDownloadUri)
            }
        }
    }

    private suspend fun updateUserInRemoteDataSource(user: User, onSuccess: suspend () -> Unit) {
        modifyUserInRemoteDataSource(user) { result ->
            if (result is DatabaseResult.Success) {
                log.d(
                    "ModifyAccountViewmodel",
                    "updateUserInRemoteDataSource: User updated successfully in remote data source"
                )
                viewModelScope.launch {
                    onSuccess()
                }
            } else {
                log.e(
                    "ModifyAccountViewmodel",
                    "updateUserInRemoteDataSource: failed to update user in remote data source"
                )
                _uiState.value = UiState.Error()
            }
        }
    }

    private suspend fun saveUserChangesInLocalDataSource(user: User) {
        modifyUserInLocalDataSource(user) { rowsModified: Int ->
            if (rowsModified > 0) {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User updated successfully in local data source"
                )
                _uiState.value = UiState.Success
            } else {
                log.e(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: failed to update user in local data source"
                )
                _uiState.value = UiState.Error()
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun logOut() {
        viewModelScope.launch {
            authUserState.collect { authUser: AuthUser? ->
                if (authUser == null) return@collect

                modifyCacheInLocalRepository(
                    LocalCache(
                        uid = authUser.uid,
                        savedBy = authUser.uid,
                        section = Section.USERS,
                        timestamp = Clock.System.now().epochSeconds
                    )
                ) { rowsUpdated: Int ->
                    if (rowsUpdated > 0) {
                        log.d(
                            "ModifyAccountViewmodel",
                            "logOut: lastLogout updated successfully in local cache"
                        )
                    } else {
                        log.e(
                            "ModifyAccountViewmodel",
                            "logOut: failed to update lastLogout in local cache"
                        )
                    }
                    signOutFromAuthDataSource()
                }
            }
        }
    }
}
