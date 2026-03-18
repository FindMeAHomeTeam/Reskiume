package com.findmeahometeam.reskiume.ui.profile.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetAllUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DeleteAccountViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getAllMyFosterHomesFromRemoteRepository: GetAllMyFosterHomesFromRemoteRepository,
    private val getAllFosterHomesFromLocalRepository: GetAllFosterHomesFromLocalRepository,
    private val deleteAllMyFosterHomesFromRemoteRepository: DeleteAllMyFosterHomesFromRemoteRepository,
    private val deleteAllMyFosterHomesFromLocalRepository: DeleteAllMyFosterHomesFromLocalRepository,
    private val getAllNonHumanAnimalsFromRemoteRepository: GetAllNonHumanAnimalsFromRemoteRepository,
    private val getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val deleteAllNonHumanAnimalsFromRemoteRepository: DeleteAllNonHumanAnimalsFromRemoteRepository,
    private val deleteAllNonHumanAnimalsFromLocalRepository: DeleteAllNonHumanAnimalsFromLocalRepository,
    private val getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    private val deleteReviewsFromRemoteRepository: DeleteReviewsFromRemoteRepository,
    private val deleteReviewsFromLocalRepository: DeleteReviewsFromLocalRepository,
    private val deleteAllCacheFromLocalRepository: DeleteAllCacheFromLocalRepository,
    private val getAllUsersFromLocalDataSource: GetAllUsersFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val deleteUserFromAuthDataSource: DeleteUserFromAuthDataSource,
    private val deleteUserFromRemoteDataSource: DeleteUserFromRemoteDataSource,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteUsersFromLocalDataSource: DeleteUsersFromLocalDataSource,
    private val log: Log
) : ViewModel() {
    private var _deletionState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val deletionState: StateFlow<UiState<Unit>> = _deletionState.asStateFlow()

    private val authUserState: Flow<AuthUser?> = observeAuthStateInAuthDataSource()

    fun deleteAccount(password: String) {
        getUserFromRemoteRepo { user ->

            // TODO delete rescue events and chats first

            manageFosterHomesDeletion(user.uid) {

                manageNonHumanAnimalsDeletion(user.uid) {

                    manageUserReviewsDeletion(user.uid) {

                        manageUserDeletion(user, password)
                    }
                }
            }
        }
    }

    private fun getUserFromRemoteRepo(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val authUser: AuthUser? = authUserState.firstOrNull()

            if (authUser == null) {
                _deletionState.value = UiState.Error()
                log.e(
                    "DeleteAccountViewmodel",
                    "getUserFromRemoteRepo: User UID is blank"
                )
                return@launch
            }
            _deletionState.value = UiState.Loading()

            val remoteUser: User? = getUserFromRemoteDataSource(authUser.uid).firstOrNull()
            if (remoteUser == null) {

                _deletionState.value = UiState.Error()
                log.e(
                    "DeleteAccountViewmodel",
                    "getUserFromRemoteRepo: User ${authUser.uid} not found in remote data source"
                )
            } else {
                onSuccess(remoteUser)
            }
        }
    }

    private fun manageFosterHomesDeletion(uid: String, onComplete: () -> Unit) {

        viewModelScope.launch {

            val allMyFosterHomes: List<FosterHome> =
                getAllMyFosterHomesFromRemoteRepository(uid).first()

            if (allMyFosterHomes.isEmpty()) {
                onComplete()
            } else {
                deleteAllMyFosterHomeImagesFromRemoteDataSource(allMyFosterHomes) {

                    deleteAllFosterHomeImagesFromLocalDataSource {

                        deleteAllMyFosterHomesFromRemoteDataSource(uid) {

                            deleteAllMyFosterHomesFromLocalDataSource(uid) {

                                onComplete()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteAllMyFosterHomeImagesFromRemoteDataSource(
        allMyFosterHomes: List<FosterHome>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            allMyFosterHomes.forEach { remoteFosterHome ->

                deleteImageFromRemoteDataSource(
                    userUid = remoteFosterHome.ownerId,
                    extraId = remoteFosterHome.id,
                    section = Section.FOSTER_HOMES,
                    currentImage = remoteFosterHome.imageUrl
                ) { isDeleted ->

                    if (isDeleted) {
                        log.d(
                            "DeleteAccountViewModel",
                            "deleteAllMyFosterHomeImagesFromRemoteDataSource: Image from the foster home ${remoteFosterHome.id} was deleted successfully in the remote data source"
                        )
                    } else {
                        log.e(
                            "DeleteAccountViewModel",
                            "deleteAllMyFosterHomeImagesFromRemoteDataSource: failed to delete the image from the foster home ${remoteFosterHome.id} in the remote data source"
                        )
                    }
                }
            }
            onComplete()
        }
    }

    private fun deleteAllFosterHomeImagesFromLocalDataSource(onComplete: () -> Unit) {
        viewModelScope.launch {

            val allMyFosterHomes: List<FosterHome> =
                getAllFosterHomesFromLocalRepository().first()

            allMyFosterHomes.forEach { localFosterHome ->

                deleteImageFromLocalDataSource(currentImagePath = localFosterHome.imageUrl) { isDeleted ->

                    if (isDeleted) {
                        log.d(
                            "DeleteAccountViewModel",
                            "deleteAllFosterHomeImagesFromLocalDataSource: Image from the foster home ${localFosterHome.id} was deleted successfully in the local data source"
                        )
                    } else {
                        log.e(
                            "DeleteAccountViewModel",
                            "deleteAllFosterHomeImagesFromLocalDataSource: failed to delete the image from the foster home ${localFosterHome.id} in the local data source"
                        )
                    }
                }
            }
            onComplete()
        }
    }

    private fun deleteAllMyFosterHomesFromRemoteDataSource(
        uid: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            deleteAllMyFosterHomesFromRemoteRepository(
                uid,
                viewModelScope
            ) { databaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteAllMyFosterHomesFromRemoteDataSource: deleted foster homes from the owner id $uid from the remote repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteAllMyFosterHomesFromRemoteDataSource: failed to delete foster homes from the owner id $uid from the remote repository: ${(databaseResult as DatabaseResult.Error).message}"
                    )
                    _deletionState.value = UiState.Error()
                }
            }
        }
    }

    private fun deleteAllMyFosterHomesFromLocalDataSource(
        uid: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            deleteAllMyFosterHomesFromLocalRepository(uid, viewModelScope) { rowsDeleted ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteAllMyFosterHomesFromLocalDataSource: deleted $rowsDeleted foster homes from the caregiver $uid from the local repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteAllMyFosterHomesFromLocalDataSource: failed to delete foster homes from the caregiver $uid from the local repository"
                    )
                    _deletionState.value = UiState.Error()
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

                    deleteAllNonHumanAnimalImagesFromLocalDataSource {

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

    private fun deleteAllNonHumanAnimalImagesFromLocalDataSource(onComplete: () -> Unit) {
        viewModelScope.launch {

            val allNonHumanAnimals: List<NonHumanAnimal> =
                getAllNonHumanAnimalsFromLocalRepository().first()

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
                _deletionState.value = UiState.Error()
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
                        "deleteAllNonHumanAnimalsFromLocalDataSource: deleted $rowsDeleted non human animals from caregiver $uid from local repository"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteAllNonHumanAnimalsFromLocalDataSource: failed to delete non human animals from caregiver $uid from local repository"
                    )
                    _deletionState.value = UiState.Error()
                }
            }
        }
    }

    private fun manageUserReviewsDeletion(uid: String, onComplete: () -> Unit) {
        viewModelScope.launch {

            val reviewList: List<Review> = getReviewsFromRemoteRepository(uid).first()

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

    private fun deleteUserReviewsFromRemoteRepository(uid: String, onCompletion: () -> Unit) {

        deleteReviewsFromRemoteRepository(uid) { result: DatabaseResult ->
            if (result is DatabaseResult.Success) {
                log.d(
                    "DeleteAccountViewmodel",
                    "deleteUserReviewsFromRemoteRepository: Deleted reviews from remote repository"
                )
            } else {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteUserReviewsFromRemoteRepository: Error deleting reviews for the user $uid from the remote repository: ${(result as DatabaseResult.Error).message}"
                )
            }
            onCompletion()
        }
    }

    private fun deleteUserReviewsFromLocalRepository(uid: String, onCompletion: () -> Unit) {
        viewModelScope.launch {

            deleteReviewsFromLocalRepository(uid) { rowsDeleted ->
                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromLocalRepository: Deleted $rowsDeleted reviews for the user $uid from the local repository"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteUserReviewsFromLocalRepository: No reviews to delete for the user $uid from the local repository"
                    )
                }
                onCompletion()
            }
        }
    }

    private fun manageUserDeletion(user: User, password: String) {

        deleteUserCacheFromLocalRepository(user.uid) {

            deleteAvatarFromRemoteDataSource(user.uid, user.image) {

                deleteAllUserAvatarsFromLocalDataSource {

                    deleteUserFromRemoteRepository(user.uid) {

                        deleteMyUserFromAuthDataSource(password) {

                            deleteMyUserFromLocalDataSource(user.uid)
                        }
                    }
                }
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
        onDeletedRemoteImage: () -> Unit
    ) {
        viewModelScope.launch {

            deleteImageFromRemoteDataSource(
                userUid = uid,
                extraId = "",
                section = Section.USERS,
                currentImage = remoteImage
            ) { isDeleted: Boolean ->

                if (isDeleted) {
                    log.d(
                        "DeleteAccountViewModel",
                        "deleteAvatarFromRemoteDataSource: Image from the user $uid was deleted successfully in the remote data source"
                    )
                } else {
                    log.e(
                        "DeleteAccountViewModel",
                        "deleteAvatarFromRemoteDataSource: failed to delete the image from the user $uid in the remote data source"
                    )
                }
                onDeletedRemoteImage()
            }
        }
    }

    private fun deleteAllUserAvatarsFromLocalDataSource(onComplete: () -> Unit) {
        viewModelScope.launch {

            val allUsers: List<User> = getAllUsersFromLocalDataSource()
            allUsers.forEach { user ->

                if (user.image.isNotBlank()) {

                    deleteImageFromLocalDataSource(currentImagePath = user.image) { isDeleted: Boolean ->
                        if (isDeleted) {
                            log.d(
                                "DeleteAccountViewModel",
                                "deleteAllUserAvatarsFromLocalDataSource: Image from the user ${user.uid} was deleted successfully in the local data source"
                            )
                        } else {
                            log.e(
                                "DeleteAccountViewModel",
                                "deleteAllUserAvatarsFromLocalDataSource: failed to delete the image from the user ${user.uid} in the local data source"
                            )
                        }
                    }
                }
            }
            onComplete()
        }
    }

    private fun deleteUserFromRemoteRepository(uid: String, onSuccess: () -> Unit) {

        deleteUserFromRemoteDataSource(uid) { result: DatabaseResult ->
            if (result is DatabaseResult.Error) {
                _deletionState.value = UiState.Error(result.message)
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteUserFromRemoteRepository: Error deleting the user $uid from the remote repository: ${result.message}"
                )
            } else {
                onSuccess()
                log.d(
                    "DeleteAccountViewmodel",
                    "deleteUserFromRemoteRepository: User $uid deleted successfully from the remote repository"
                )
            }
        }
    }

    private fun deleteMyUserFromAuthDataSource(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {

            deleteUserFromAuthDataSource(password) { errorMessage: String ->
                if (errorMessage.isNotBlank()) {
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromAuthDataSource: Error deleting the user from auth data source: $errorMessage"
                    )
                } else {
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromAuthDataSource: User deleted successfully from the auth data source"
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
                    _deletionState.value = UiState.Error()
                    log.e(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromLocalDataSource: Error deleting the user $deletedUid from the local data source"
                    )
                } else {
                    _deletionState.value = UiState.Success(Unit)
                    log.d(
                        "DeleteAccountViewmodel",
                        "deleteMyUserFromLocalDataSource: User $deletedUid deleted successfully from the local data source"
                    )
                }
            }
        }
    }
}
