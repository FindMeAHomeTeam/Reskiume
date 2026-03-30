package com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInRemoteRepository
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
import reskiume.composeapp.generated.resources.create_rescue_event_screen_turn_on_location
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateRescueEventViewmodel(
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val observeIfLocationEnabledFromLocationRepository: ObserveIfLocationEnabledFromLocationRepository,
    private val requestEnableLocationFromLocationRepository: RequestEnableLocationFromLocationRepository,
    private val getLocationFromLocationRepository: GetLocationFromLocationRepository,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getStringProvider: StringProvider,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertRescueEventInRemoteRepository: InsertRescueEventInRemoteRepository,
    private val insertRescueEventInLocalRepository: InsertRescueEventInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val log: Log
) : ViewModel() {

    val allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow: Flow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository().map {
            it.mapNotNull { nonHumanAnimal ->
                if (nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.NEEDS_TO_BE_REHOMED) {
                    nonHumanAnimal
                } else {
                    null
                }
            }
        }

    private var rescueEventLongitude: Double = 0.0

    private var rescueEventLatitude: Double = 0.0

    private val _saveChangesUiState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val saveChangesUiState: StateFlow<UiState<Unit>> = _saveChangesUiState.asStateFlow()

    fun observeIfLocationEnabled(): Flow<Boolean> = observeIfLocationEnabledFromLocationRepository()

    suspend fun requestEnableLocation(): Boolean {

        return suspendCoroutine { continuation ->
            requestEnableLocationFromLocationRepository { isEnabled: Boolean ->
                continuation.resume(isEnabled)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun updateLocation() {
        val locationPair: Pair<Double, Double> = getLocationFromLocationRepository()
        rescueEventLongitude = locationPair.first
        rescueEventLatitude = locationPair.second

        log.d(
            "CreateRescueEventViewmodel",
            "Longitude and latitude: $locationPair"
        )
    }

    fun createRescueEvent(createdRescueEvent: RescueEvent) {

        _saveChangesUiState.value = UiState.Loading()

        updateRescueEventData(createdRescueEvent) { updatedRescueEvent ->

            uploadNewImageToRemoteDataSource(updatedRescueEvent) { rescueEventWithPossibleImageDownloadUri: RescueEvent ->

                createRescueEventInRemoteDataSource(
                    rescueEventWithPossibleImageDownloadUri
                ) {
                    createRescueEventInLocalDataSource(updatedRescueEvent) {

                        createCacheForRescueEventInLocalDataSource(updatedRescueEvent)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun updateRescueEventData(
        createdRescueEvent: RescueEvent,
        onComplete: (RescueEvent) -> Unit
    ) {
        viewModelScope.launch {
            val creatorId = observeAuthStateInAuthDataSource().first()!!.uid
            val rescueEventId = Clock.System.now().epochSeconds.toString() + creatorId

            if (rescueEventLongitude == 0.0 || rescueEventLatitude == 0.0) {
                val errorMessage =
                    getStringProvider.getStringResource(Res.string.create_rescue_event_screen_turn_on_location)
                log.d(
                    "CreateRescueEventViewmodel",
                    errorMessage
                )
                _saveChangesUiState.value = UiState.Error(errorMessage)
                return@launch
            }

            val updatedRescueEvent = createdRescueEvent.copy(
                id = rescueEventId,
                creatorId = creatorId,
                allNonHumanAnimalsToRescue = createdRescueEvent.allNonHumanAnimalsToRescue.map {
                    it.copy(rescueEventId = rescueEventId)
                },
                allNeedsToCover = createdRescueEvent.allNeedsToCover.map {
                    it.copy(rescueEventId = rescueEventId)
                },
                longitude = rescueEventLongitude,
                latitude = rescueEventLatitude
            )
            onComplete(updatedRescueEvent)
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        createdRescueEvent: RescueEvent,
        onComplete: (RescueEvent) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = createdRescueEvent.creatorId,
            extraId = createdRescueEvent.id,
            section = Section.RESCUE_EVENTS,
            imageUri = createdRescueEvent.imageUrl
        ) { imageDownloadUri: String ->

            val rescueEventWithPossibleImageDownloadUri: RescueEvent =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "CreateRescueEventViewmodel",
                        "uploadNewImageToRemoteDataSource: the download URI from the rescue event ${createdRescueEvent.id} is blank"
                    )
                    createdRescueEvent
                } else {
                    log.d(
                        "CreateRescueEventViewmodel",
                        "uploadNewImageToRemoteDataSource: the download URI from the rescue event ${createdRescueEvent.id} was saved successfully"
                    )
                    createdRescueEvent.copy(imageUrl = imageDownloadUri)
                }
            onComplete(rescueEventWithPossibleImageDownloadUri)
        }
    }

    private fun createRescueEventInRemoteDataSource(
        createdRescueEvent: RescueEvent,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            insertRescueEventInRemoteRepository(
                createdRescueEvent,
                viewModelScope
            ) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "CreateRescueEventViewmodel",
                        "createRescueEventInRemoteDataSource: rescue event ${createdRescueEvent.id} created successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateRescueEventViewmodel",
                        "createRescueEventInRemoteDataSource: failed to create the rescue event ${createdRescueEvent.id} in the remote data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun createRescueEventInLocalDataSource(
        updatedRescueEvent: RescueEvent,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            insertRescueEventInLocalRepository(
                updatedRescueEvent,
                viewModelScope
            ) { isUpdated: Boolean ->

                if (isUpdated) {
                    log.d(
                        "CreateRescueEventViewmodel",
                        "createRescueEventInLocalDataSource: rescue event ${updatedRescueEvent.id} created successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateRescueEventViewmodel",
                        "createRescueEventInLocalDataSource: failed to create the rescue event ${updatedRescueEvent.id} in the local data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createCacheForRescueEventInLocalDataSource(rescueEvent: RescueEvent) {

        viewModelScope.launch {

            insertCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = rescueEvent.id,
                    savedBy = rescueEvent.creatorId,
                    section = Section.RESCUE_EVENTS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId: Long ->

                if (rowId > 0) {
                    log.d(
                        "CreateRescueEventViewmodel",
                        "createCacheForRescueEventInLocalDataSource: ${rescueEvent.id} created in the local cache in section ${Section.RESCUE_EVENTS}"
                    )
                } else {
                    log.e(
                        "CreateRescueEventViewmodel",
                        "createCacheForRescueEventInLocalDataSource: Error creating ${rescueEvent.id} in the local cache in section ${Section.RESCUE_EVENTS}"
                    )
                }
                _saveChangesUiState.value = UiState.Success(Unit)
            }
        }
    }
}
