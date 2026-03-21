package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ModifyRescueEventViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val getRescueEventFromLocalRepository: GetRescueEventFromLocalRepository,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val getRescueEventFromRemoteRepository: GetRescueEventFromRemoteRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val modifyRescueEventInRemoteRepository: ModifyRescueEventInRemoteRepository,
    private val modifyRescueEventInLocalRepository: ModifyRescueEventInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val deleteRescueEventUtil: DeleteRescueEventUtil,
    private val log: Log
) : ViewModel() {

    private val rescueEventId: String =
        saveStateHandleProvider.provideObjectRoute(ModifyRescueEvent::class).rescueEventId

    val rescueEventFlow: Flow<UiState<UiRescueEvent>> =
        getRescueEventFromLocalRepository(
            rescueEventId
        ).map { rescueEvent: RescueEvent? ->
            UiRescueEvent(
                rescueEvent = rescueEvent!!.copy(
                    imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                        rescueEvent.imageUrl
                    } else {
                        getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                    }
                ),
                allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        nonHumanAnimalToRescue.nonHumanAnimalId,
                        nonHumanAnimalToRescue.caregiverId,
                        viewModelScope
                    ).firstOrNull()
                }
            )
        }.toUiState()

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

    private val _manageChangesUiState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val manageChangesUiState: StateFlow<UiState<Unit>> = _manageChangesUiState.asStateFlow()

    fun saveRescueEventChanges(
        isDifferentImage: Boolean,
        modifiedRescueEvent: RescueEvent
    ) {
        viewModelScope.launch {
            _manageChangesUiState.value = UiState.Loading()

            if (isDifferentImage) {

                deleteCurrentImageFromRemoteDataSource(
                    modifiedRescueEvent.creatorId,
                    modifiedRescueEvent.id
                ) {
                    deleteCurrentImageFromLocalDataSource(modifiedRescueEvent.id) {

                        uploadNewImageToRemoteDataSource(modifiedRescueEvent) { rescueEventWithPossibleImageDownloadUri: RescueEvent ->

                            modifyRescueEventInRemoteDataSource(
                                rescueEventWithPossibleImageDownloadUri
                            ) {
                                modifyRescueEventInLocalDataSource(modifiedRescueEvent) {

                                    modifyCacheForRescueEventInLocalDataSource(modifiedRescueEvent)
                                }
                            }
                        }
                    }
                }
            } else {
                val collectedRescueEvent = getRescueEventFromRemoteRepository(
                    modifiedRescueEvent.id
                ).first()

                modifyRescueEventInRemoteDataSource(
                    modifiedRescueEvent.copy(imageUrl = collectedRescueEvent.imageUrl)
                ) {
                    modifyRescueEventInLocalDataSource(modifiedRescueEvent) {

                        modifyCacheForRescueEventInLocalDataSource(modifiedRescueEvent)
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromRemoteDataSource(
        creatorId: String,
        rescueEventId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val remoteRescueEvent = getRescueEventFromRemoteRepository(
                rescueEventId
            ).first()

            deleteImageFromRemoteDataSource(
                userUid = creatorId,
                extraId = rescueEventId,
                section = Section.RESCUE_EVENTS,
                currentImage = remoteRescueEvent.imageUrl
            ) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "deleteCurrentImageFromRemoteDataSource: Image from the rescue event $rescueEventId was deleted successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyRescueEventViewModel",
                        "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event $rescueEventId in the remote data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        rescueEventId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val localRescueEvent: RescueEvent? =
                getRescueEventFromLocalRepository(
                    rescueEventId
                ).firstOrNull()

            if (localRescueEvent == null) {
                log.e(
                    "ModifyRescueEventViewModel",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event $rescueEventId in the local data source because the local rescue event does not exist!"
                )
                _manageChangesUiState.value = UiState.Error()
                return@launch
            }

            deleteImageFromLocalDataSource(currentImagePath = localRescueEvent.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "deleteCurrentImageFromLocalDataSource: Image from the rescue event $rescueEventId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyRescueEventViewModel",
                        "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event $rescueEventId in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        modifiedRescueEvent: RescueEvent,
        onComplete: (RescueEvent) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = modifiedRescueEvent.creatorId,
            extraId = modifiedRescueEvent.id,
            section = Section.RESCUE_EVENTS,
            imageUri = modifiedRescueEvent.imageUrl
        ) { imageDownloadUri: String ->

            val rescueEventWithPossibleImageDownloadUri: RescueEvent =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the rescue event ${modifiedRescueEvent.id} is blank"
                    )
                    modifiedRescueEvent
                } else {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the rescue event ${modifiedRescueEvent.id} was saved successfully"
                    )
                    modifiedRescueEvent.copy(imageUrl = imageDownloadUri)
                }
            onComplete(rescueEventWithPossibleImageDownloadUri)
        }
    }

    private fun modifyRescueEventInRemoteDataSource(
        updatedRescueEvent: RescueEvent,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            modifyRescueEventInRemoteRepository(
                updatedRescueEvent,
                viewModelScope
            ) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "modifyRescueEventInRemoteDataSource: rescue event ${updatedRescueEvent.id} updated successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyRescueEventViewModel",
                        "modifyRescueEventInRemoteDataSource: failed to update the rescue event ${updatedRescueEvent.id} in the remote data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun modifyRescueEventInLocalDataSource(
        updatedRescueEvent: RescueEvent,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val previousRescueEvent =
                getRescueEventFromLocalRepository(updatedRescueEvent.id).first()!!

            modifyRescueEventInLocalRepository(
                updatedRescueEvent,
                previousRescueEvent,
                viewModelScope
            ) { isUpdated: Boolean ->

                if (isUpdated) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "modifyRescueEventInLocalDataSource: rescue event ${updatedRescueEvent.id} updated successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyRescueEventViewModel",
                        "modifyRescueEventInLocalDataSource: failed to update the rescue event ${updatedRescueEvent.id} in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun modifyCacheForRescueEventInLocalDataSource(rescueEvent: RescueEvent) {

        viewModelScope.launch {

            modifyCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = rescueEvent.id,
                    savedBy = rescueEvent.creatorId,
                    section = Section.RESCUE_EVENTS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowsUpdated: Int ->

                if (rowsUpdated > 0) {
                    log.d(
                        "ModifyRescueEventViewModel",
                        "modifyCacheForRescueEventInLocalDataSource: ${rescueEvent.id} updated in local cache in section ${Section.RESCUE_EVENTS}"
                    )
                } else {
                    log.e(
                        "ModifyRescueEventViewModel",
                        "modifyCacheForRescueEventInLocalDataSource: Error updating ${rescueEvent.id} in local cache in section ${Section.RESCUE_EVENTS}"
                    )
                }
                _manageChangesUiState.value = UiState.Success(Unit)
            }
        }
    }

    fun deleteRescueEvent(id: String, creatorId: String) {

        _manageChangesUiState.value = UiState.Loading()

        deleteRescueEventUtil.deleteRescueEvent(
            id = id,
            creatorId = creatorId,
            coroutineScope = viewModelScope,
            onError = {
                _manageChangesUiState.value = UiState.Error()
            },
            onComplete = {
                _manageChangesUiState.value = UiState.Success(Unit)
            }
        )
    }
}
