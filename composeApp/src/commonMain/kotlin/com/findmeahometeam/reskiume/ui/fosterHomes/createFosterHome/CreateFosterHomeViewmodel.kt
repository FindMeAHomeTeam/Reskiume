package com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_foster_home_screen_turn_on_location
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateFosterHomeViewmodel(
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val requestEnableLocationFromLocationRepository: RequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getStringProvider: StringProvider,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertFosterHomeInRemoteRepository: InsertFosterHomeInRemoteRepository,
    private val insertFosterHomeInLocalRepository: InsertFosterHomeInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val log: Log
) : ViewModel() {

    val allAvailableNonHumanAnimalsLookingForAdoptionFlow: Flow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository().map {
            it.mapNotNull { nonHumanAnimal ->
                if (nonHumanAnimal.adoptionState == AdoptionState.LOOKING_FOR_ADOPTION) {
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

    fun observeIfLocationEnabled(): Flow<Boolean> = observeIfLocationEnabledFromLocationRepository()

    fun requestEnableLocation(onResul: (isEnabled: Boolean) -> Unit) {

        requestEnableLocationFromLocationRepository(onResul)
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

                        createCacheForFosterHomeInLocalDataSource(updatedFosterHome)
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
                val errorMessage =
                    getStringProvider.getStringResource(Res.string.create_foster_home_screen_turn_on_location)
                log.d(
                    "CheckAllFosterHomesViewmodel",
                    errorMessage
                )
                _saveChangesUiState.value = UiState.Error(errorMessage)
                return@launch
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

            val fosterHomeWithPossibleImageDownloadUri: FosterHome =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "CreateFosterHomeViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the foster home ${createdFosterHome.id} is blank"
                    )
                    createdFosterHome
                } else {
                    log.d(
                        "CreateFosterHomeViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the foster home ${createdFosterHome.id} was saved successfully"
                    )
                    createdFosterHome.copy(imageUrl = imageDownloadUri)
                }
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
                        "CreateFosterHomeViewModel",
                        "createFosterHomeInRemoteDataSource: foster home ${createdFosterHome.id} created successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateFosterHomeViewModel",
                        "createFosterHomeInRemoteDataSource: failed to update the foster home ${createdFosterHome.id} in the remote data source"
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
                        "CreateFosterHomeViewModel",
                        "createFosterHomeInLocalDataSource: foster home ${updatedFosterHome.id} created successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateFosterHomeViewModel",
                        "createFosterHomeInLocalDataSource: failed to create the foster home ${updatedFosterHome.id} in the local data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createCacheForFosterHomeInLocalDataSource(fosterHome: FosterHome) {

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
                        "CreateFosterHomeViewModel",
                        "createCacheForFosterHomeInLocalDataSource: ${fosterHome.id} created in the local cache in section ${Section.FOSTER_HOMES}"
                    )
                } else {
                    log.e(
                        "CreateFosterHomeViewModel",
                        "createCacheForFosterHomeInLocalDataSource: Error creating ${fosterHome.id} in the local cache in section ${Section.FOSTER_HOMES}"
                    )
                }
                _saveChangesUiState.value = UiState.Success(Unit)
            }
        }
    }
}
