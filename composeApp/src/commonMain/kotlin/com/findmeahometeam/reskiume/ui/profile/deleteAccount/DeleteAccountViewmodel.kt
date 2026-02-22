package com.findmeahometeam.reskiume.ui.profile.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeleteAccountViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getAllNonHumanAnimalsFromRemoteRepository: GetAllNonHumanAnimalsFromRemoteRepository,
    private val deleteAllNonHumanAnimalsFromRemoteRepository: DeleteAllNonHumanAnimalsFromRemoteRepository,
    private val deleteAllNonHumanAnimalsFromLocalRepository: DeleteAllNonHumanAnimalsFromLocalRepository,
    private val getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    private val deleteReviewsFromRemoteRepository: DeleteReviewsFromRemoteRepository,
    private val deleteReviewsFromLocalRepository: DeleteReviewsFromLocalRepository,
    private val deleteAllCacheFromLocalRepository: DeleteAllCacheFromLocalRepository,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteUsersFromLocalDataSource: DeleteUsersFromLocalDataSource,
    private val log: Log
) : ViewModel() {
    private var _state: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    private val authUserState: Flow<AuthUser?> = observeAuthStateInAuthDataSource()

    fun deleteAccount(password: String) {
        getUserFromRemoteDatasource { user ->

            // TODO delete foster homes and events first

            manageNonHumanAnimalsDeletion(user.uid) {

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
    }

    private fun manageNonHumanAnimalsDeletion(uid: String, onComplete: () -> Unit) {

        viewModelScope.launch {

            val allNonHumanAnimals: List<NonHumanAnimal> =
                getAllNonHumanAnimalsFromRemoteRepository(uid).first()

            if (allNonHumanAnimals.isEmpty()) {
                onComplete()
            } else {
                deleteAllNonHumanAnimalImagesFromRemoteDataSource(allNonHumanAnimals) {

                    deleteAllNonHumanAnimalImagesFromLocalDataSource(allNonHumanAnimals) {

                        deleteAllNonHumanAnimalsFromRemoteDataSource(uid) {

                            deleteAllNonHumanAnimalsFromLocalDataSource(uid) {

                                onComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteAllNonHumanAnimalImagesFromRemoteDataSource(
        allNonHumanAnimals: List<NonHumanAnimal>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            allNonHumanAnimals.forEach { remoteNonHumanAnimal ->

                deleteImageFromRemoteDataSource(
                    userUid = remoteNonHumanAnimal.caregiverId,
                    extraId = remoteNonHumanAnimal.id,
                    section = Section.NON_HUMAN_ANIMALS,
                    currentImage = remoteNonHumanAnimal.imageUrl
                ) { isDeleted ->

                    if (isDeleted) {
                        log.d(
                            "DeleteAccountViewModel",
                            "deleteAllNonHumanAnimalImagesFromRemoteDataSource: Image from the non human animal ${remoteNonHumanAnimal.id} was deleted successfully in the remote data source"
                        )
                    } else {
                        log.e(
                            "DeleteAccountViewModel",
                            "deleteAllNonHumanAnimalImagesFromRemoteDataSource: failed to delete the image from the non human animal ${remoteNonHumanAnimal.id} in the remote data source"
                        )
                    }
                }
            }
            onComplete()
        }
    }

    private fun deleteAllNonHumanAnimalImagesFromLocalDataSource(
        allNonHumanAnimals: List<NonHumanAnimal>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            allNonHumanAnimals.forEach { localNonHumanAnimal ->

                deleteImageFromLocalDataSource(currentImagePath = localNonHumanAnimal.imageUrl) { isDeleted ->

                    if (isDeleted) {
                        log.d(
                            "DeleteAccountViewModel",
                            "deleteAllNonHumanAnimalImagesFromLocalDataSource: Image from the non human animal ${localNonHumanAnimal.id} was deleted successfully in the local data source"
                        )
                    } else {
                        log.e(
                            "DeleteAccountViewModel",
                            "deleteAllNonHumanAnimalImagesFromLocalDataSource: failed to delete the image from the non human animal ${localNonHumanAnimal.id} in the local data source"
                        )
                    }
                }
            }
            onComplete()
        }
    }

    private fun deleteAllNonHumanAnimalsFromRemoteDataSource(
        uid: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            deleteAllNonHumanAnimalsFromRemoteRepository(uid) { databaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteAllNonHumanAnimalsFromRemoteDataSource: deleted non human animals from caregiver $uid from remote repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteAllNonHumanAnimalsFromRemoteDataSource: failed to delete non human animals from caregiver $uid from remote repository: ${(databaseResult as DatabaseResult.Error).message}"
                    )
                    _state.value = UiState.Error()
                }
            }
        }
    }

    private fun deleteAllNonHumanAnimalsFromLocalDataSource(
        uid: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            deleteAllNonHumanAnimalsFromLocalRepository(uid) { rowsDeleted ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteNonHumanAnimalsFromLocalRepository: deleted $rowsDeleted non human animals from caregiver $uid from local repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteNonHumanAnimalsFromLocalRepository: failed to delete non human animals from caregiver $uid from local repository"
                    )
                    _state.value = UiState.Error()
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
                _state.value = UiState.Loading()
                getUserFromRemoteDataSource(authUser.uid).collect { remoteUser: User? ->

                    if (remoteUser == null) {
                        _state.value = UiState.Error()
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

            deleteAllCacheFromLocalRepository(uid) { rowsDeleted ->
                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteUserCacheFromLocalRepository: Deleted $rowsDeleted cache entries for $uid from local repository"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserCacheFromLocalRepository: Error deleting entries for $uid from local repository"
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
                userUid = uid,
                extraId = "",
                section = Section.USERS,
                currentImage = remoteImage
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
            deleteImageFromLocalDataSource(currentImagePath = user.image) { isDeleted: Boolean ->
                if (!isDeleted) {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteAvatarFromLocalDataSource: failed to delete image in local data source"
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
                    _state.value = UiState.Success(Unit)
                    log.d(
                        "DeleteAccountViewmodel",
                        "User deleted successfully"
                    )
                }
            }
        }
    }
}
