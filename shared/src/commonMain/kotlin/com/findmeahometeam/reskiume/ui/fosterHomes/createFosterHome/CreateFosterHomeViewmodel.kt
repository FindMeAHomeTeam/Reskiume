package com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetNonHumanAnimalInfoInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveRequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.create_foster_home_screen_turn_on_location
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateFosterHomeViewmodel(
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val observeRequestEnableLocationFromLocationRepository: ObserveRequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getStringProvider: StringProvider,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertFosterHomeInRemoteRepository: InsertFosterHomeInRemoteRepository,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val subscriptionManagerUtil: SubscriptionManagerUtil,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val getNonHumanAnimalInfoInLocalRepository: GetNonHumanAnimalInfoInLocalRepository,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val log: Log
) : ViewModel() {

    val allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow: Flow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository().map {
            it.mapNotNull { nonHumanAnimal ->
                if (nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                    && getNonHumanAnimalInfoInLocalRepository(nonHumanAnimal.id).firstOrNull() == null
                ) {
                    nonHumanAnimal
                } else {
                    null
                }
            }
        }

    private var fosterHomeLongitude: Double = 0.0

    private var fosterHomeLatitude: Double = 0.0

    private val _saveChangesUiState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val saveChangesUiState: StateFlow<UiState<Unit>> = _saveChangesUiState.asStateFlow()

    val userState: Flow<User?> = observeAuthStateInAuthDataSource().map { authUser ->

        val user = if (authUser != null) getUserFromLocalDataSource(authUser.uid).firstOrNull() else null
        if (user == null || !user.isLoggedIn) {
            null
        } else {
            user
        }
    }

    fun observeIfLocationEnabled(): Flow<Boolean> = observeIfLocationEnabledFromLocationRepository()

    fun requestEnableLocation() {
        observeRequestEnableLocationFromLocationRepository().launchIn(viewModelScope)
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateLocation() {
        val locationPair: Pair<Double, Double> = getLocationFromLocationRepository()
        fosterHomeLongitude = locationPair.first
        fosterHomeLatitude = locationPair.second

        log.d(
            "CreateFosterHomeViewmodel",
            "Longitude and latitude: $locationPair"
        )
    }

    fun createFosterHome(createdFosterHome: FosterHome) {

        _saveChangesUiState.value = UiState.Loading()

        updateFosterHomeData(createdFosterHome) { updatedFosterHome ->

            uploadNewImageToRemoteDataSource(updatedFosterHome) { fosterHomeWithPossibleImageDownloadUri: FosterHome ->

                createFosterHomeInRemoteDataSource(
                    fosterHomeWithPossibleImageDownloadUri
                ) {
                    createFosterHomeInLocalDataSource(updatedFosterHome) {

                        createCacheForFosterHomeInLocalDataSource(updatedFosterHome) {

                            subscribeOwnerToTheirFosterHome(updatedFosterHome.id)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun updateFosterHomeData(
        createdFosterHome: FosterHome,
        onComplete: (FosterHome) -> Unit
    ) {
        viewModelScope.launch {
            val ownerId = observeAuthStateInAuthDataSource().first()!!.uid
            val fosterHomeId = Clock.System.now().epochSeconds.toString() + ownerId

            if (fosterHomeLongitude == 0.0 || fosterHomeLatitude == 0.0) {

                updateLocation()

                if (fosterHomeLongitude == 0.0 || fosterHomeLatitude == 0.0) {
                    val errorMessage =
                        getStringProvider.getStringResource(Res.string.create_foster_home_screen_turn_on_location)
                    log.d(
                        "CreateFosterHomeViewmodel",
                        errorMessage
                    )
                    _saveChangesUiState.value = UiState.Error(errorMessage)
                    return@launch
                }
            }

            val updatedFosterHome = createdFosterHome.copy(
                id = fosterHomeId,
                ownerId = ownerId,
                allAcceptedNonHumanAnimals = createdFosterHome.allAcceptedNonHumanAnimals.map {
                    it.copy(fosterHomeId = fosterHomeId)
                },
                allResidentNonHumanAnimals = createdFosterHome.allResidentNonHumanAnimals.map {
                    it.copy(fosterHomeId = fosterHomeId)
                },
                longitude = fosterHomeLongitude,
                latitude = fosterHomeLatitude
            )
            onComplete(updatedFosterHome)
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        createdFosterHome: FosterHome,
        onComplete: (FosterHome) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = createdFosterHome.ownerId,
            extraId = createdFosterHome.id,
            section = Section.FOSTER_HOMES,
            imageUri = createdFosterHome.imageUrl
        ) { imageDownloadUri: String ->

            val imageUri: String = if (imageDownloadUri.isBlank()) {
                log.d(
                    "CreateFosterHomeViewmodel",
                    "uploadNewImageToRemoteDataSource: the download URI from the foster home ${createdFosterHome.id} is blank"
                )
                ""
            } else {
                log.d(
                    "CreateFosterHomeViewmodel",
                    "uploadNewImageToRemoteDataSource: the download URI from the foster home ${createdFosterHome.id} was saved successfully"
                )
                imageDownloadUri
            }
            val fosterHomeWithPossibleImageDownloadUri: FosterHome =
                createdFosterHome.copy(imageUrl = imageUri)
            onComplete(fosterHomeWithPossibleImageDownloadUri)
        }
    }

    private fun createFosterHomeInRemoteDataSource(
        createdFosterHome: FosterHome,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            insertFosterHomeInRemoteRepository(
                createdFosterHome,
                viewModelScope
            ) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "CreateFosterHomeViewmodel",
                        "createFosterHomeInRemoteDataSource: foster home ${createdFosterHome.id} created successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateFosterHomeViewmodel",
                        "createFosterHomeInRemoteDataSource: failed to create the foster home ${createdFosterHome.id} in the remote data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun createFosterHomeInLocalDataSource(
        updatedFosterHome: FosterHome,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            insertFosterHomeInLocalRepository(
                updatedFosterHome,
                viewModelScope
            ) { isUpdated: Boolean ->

                if (isUpdated) {
                    log.d(
                        "CreateFosterHomeViewmodel",
                        "createFosterHomeInLocalDataSource: foster home ${updatedFosterHome.id} created successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateFosterHomeViewmodel",
                        "createFosterHomeInLocalDataSource: failed to create the foster home ${updatedFosterHome.id} in the local data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createCacheForFosterHomeInLocalDataSource(
        fosterHome: FosterHome,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {

            insertCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = fosterHome.id,
                    savedBy = fosterHome.ownerId,
                    section = Section.FOSTER_HOMES,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId: Long ->

                if (rowId > 0) {
                    log.d(
                        "CreateFosterHomeViewmodel",
                        "createCacheForFosterHomeInLocalDataSource: ${fosterHome.id} created in the local cache in section ${Section.FOSTER_HOMES}"
                    )
                } else {
                    log.e(
                        "CreateFosterHomeViewmodel",
                        "createCacheForFosterHomeInLocalDataSource: Error creating ${fosterHome.id} in the local cache in section ${Section.FOSTER_HOMES}"
                    )
                }
                onComplete()
            }
        }
    }

    private fun subscribeOwnerToTheirFosterHome(fosterHomeId: String) {
        viewModelScope.launch {

            val ownerId = observeAuthStateInAuthDataSource().first()!!.uid
            val owner = getUserFromRemoteDataSource(ownerId).first()!!
            subscriptionManagerUtil.subscribeToTopic(owner, fosterHomeId, viewModelScope) {

                _saveChangesUiState.value = UiState.Success(Unit)
            }
        }
    }

    fun deleteLocalImage(uriToDelete: String) {

        deleteImageFromLocalDataSource(uriToDelete) { isDeleted ->

            if (isDeleted) {
                log.d(
                    "CreateFosterHomeViewModel",
                    "deleteLocalImage: the image $uriToDelete was deleted successfully in the local data source"
                )
            } else {
                log.e(
                    "CreateFosterHomeViewModel",
                    "deleteLocalImage: failed to delete the image $uriToDelete in the local data source"
                )
            }
        }
    }
}
