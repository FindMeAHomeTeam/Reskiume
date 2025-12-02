package com.findmeahometeam.reskiume.ui.profile.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    private val getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    private val deleteReviewsFromRemoteRepository: DeleteReviewsFromRemoteRepository,
    private val deleteReviewsFromLocalRepository: DeleteReviewsFromLocalRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageInLocalDataSource: DeleteImageInLocalDataSource,
    private val deleteUsersFromLocalDataSource: DeleteUsersFromLocalDataSource,
    private val log: Log
) : ViewModel() {
    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val authUserState: Flow<AuthUser?> = observeAuthStateFromAuthDataSource()

    fun deleteAccount(password: String) {
        getUserFromRemoteDatasource { user ->

            // TODO delete foster homes, events and non-human animals first

            manageUserReviewsDeletion(user.uid) {

                deleteUserCacheFromLocalRepository(user.uid) {

                    deleteAvatarFromRemoteDataSource(
                        uid = user.uid,
                        remoteImage = user.image,
                        onEmptyImage = {
                            manageUserDeletion(user.uid, password)
                        },
                        onDeletedRemoteImage = {

                            deleteAvatarFromLocalDataSource(user.uid) {

                                manageUserDeletion(user.uid, password)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun manageUserReviewsDeletion(uid: String, onComplete: () -> Unit) {
        viewModelScope.launch {

            getReviewsFromRemoteRepository(uid).collect { reviewList ->

                if (reviewList.isEmpty()) {

                    onComplete()
                } else {
                    deleteUserReviewsFromRemoteRepository(uid) {

                        deleteUserReviewsFromLocalRepository(uid) {

                            onComplete()
                        }
                    }
                }
            }
        }
    }

    private fun manageUserDeletion(uid: String, password: String) {
        deleteUserFromRemoteRepository(uid) {

            deleteMyUserFromAuthDataSource(password) {

                deleteMyUserFromLocalDataSource(uid)
            }
        }
    }

    private fun getUserFromRemoteDatasource(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            authUserState.collect { authUser: AuthUser? ->

                if (authUser == null) {
                    _state.value = UiState.Error()
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserFromRemoteDataSource: User UID is blank"
                    )
                    return@collect
                }
                _state.value = UiState.Loading
                getUserFromRemoteDataSource(authUser.uid).collect { remoteUser: User? ->

                    if (remoteUser == null) {
                        _state.value = UiState.Error("User not found in remote data source")
                        log.e(
                            "DeleteAccountViewmodel",
                            "deleteUserFromRemoteDataSource: User not found in remote data source"
                        )
                    } else {
                        onSuccess(remoteUser)
                    }
                }
            }
        }
    }

    private fun deleteUserReviewsFromRemoteRepository(uid: String, onCompletion: () -> Unit) {
        viewModelScope.launch {
            deleteReviewsFromRemoteRepository(uid) { result: DatabaseResult ->
                if (result is DatabaseResult.Success) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromRemoteRepository: Deleted reviews from remote repository"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromRemoteRepository: ${(result as DatabaseResult.Error).message}"
                    )
                }
                onCompletion()
            }
        }
    }

    private fun deleteUserReviewsFromLocalRepository(uid: String, onCompletion: () -> Unit) {
        viewModelScope.launch {
            deleteReviewsFromLocalRepository(uid) { rowsDeleted ->
                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromLocalRepository: Deleted $rowsDeleted reviews from local repository"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromLocalRepository: No reviews to delete from local repository"
                    )
                }
                onCompletion()
            }
        }
    }

    private fun deleteUserCacheFromLocalRepository(uid: String, onCompletion: () -> Unit) {

        viewModelScope.launch {

            deleteCacheFromLocalRepository(uid) { rowsDeleted ->
                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteUserCacheInLocalRepository: Deleted $rowsDeleted cache entries from local repository"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserCacheInLocalRepository: No cache entries to delete from local repository"
                    )
                }
                onCompletion()
            }
        }
    }

    private fun deleteAvatarFromRemoteDataSource(
        uid: String,
        remoteImage: String,
        onEmptyImage: () -> Unit,
        onDeletedRemoteImage: () -> Unit
    ) {
        viewModelScope.launch {

            if (remoteImage.isBlank()) {
                onEmptyImage()
                return@launch
            }

            deleteImageFromRemoteDataSource(
                uid,
                Section.USERS,
                remoteImage
            ) { imageDeleted: Boolean ->
                if (!imageDeleted) {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteImageFromRemoteDataSource: Error deleting user image from remote data source"
                    )
                }
                onDeletedRemoteImage()
            }
        }
    }

    private fun deleteAvatarFromLocalDataSource(
        uid: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {

            val user: User? = getUserFromLocalDataSource(uid)

            if (user?.image.isNullOrBlank()) {
                onSuccess()
                return@launch
            }
            deleteImageInLocalDataSource(
                userUid = uid,
                currentImagePath = user.image
            ) { isDeleted: Boolean ->
                if (!isDeleted) {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteImageInLocalDataSource: failed to delete image in local data source"
                    )
                }
                onSuccess()
            }
        }
    }

    private fun deleteUserFromRemoteRepository(uid: String, onSuccess: () -> Unit) {

        deleteUserFromRemoteDataSource(uid) { result: DatabaseResult ->
            if (result is DatabaseResult.Error) {
                _state.value = UiState.Error(result.message)
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteUserFromRemoteDataSource: ${result.message}"
                )
            } else {
                onSuccess()
            }
        }
    }

    private fun deleteMyUserFromAuthDataSource(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteUserFromAuthDataSource(password) { errorMessage: String ->
                if (errorMessage.isNotBlank()) {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromAuthDataSource: $errorMessage"
                    )
                }
                onSuccess()
            }
        }
    }


    private fun deleteMyUserFromLocalDataSource(deletedUid: String) {
        viewModelScope.launch {

            deleteUsersFromLocalDataSource(deletedUid) { rowsDeleted: Int ->

                if (rowsDeleted == 0) {
                    _state.value = UiState.Error()
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromLocalDataSource: Error deleting user from local data source"
                    )
                } else {
                    _state.value = UiState.Success
                    log.d(
                        "DeleteAccountViewmodel",
                        "User deleted successfully"
                    )
                }
            }
        }
    }
}
