package com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ModifyNonHumanAnimalViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil,
    private val getNonHumanAnimalFromRemoteRepository: GetNonHumanAnimalFromRemoteRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val getNonHumanAnimalFromLocalRepository: GetNonHumanAnimalFromLocalRepository,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val modifyNonHumanAnimalInRemoteRepository: ModifyNonHumanAnimalInRemoteRepository,
    private val modifyNonHumanAnimalInLocalRepository: ModifyNonHumanAnimalInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val log: Log
) : ViewModel() {

    private val nonHumanAnimalId: String =
        saveStateHandleProvider.provideObjectRoute(ModifyNonHumanAnimal::class).nonHumanAnimalId

    private val caregiverId: String =
        saveStateHandleProvider.provideObjectRoute(ModifyNonHumanAnimal::class).caregiverId

    val nonHumanAnimalFlow: Flow<UiState<NonHumanAnimal>> =
        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
            nonHumanAnimalId,
            caregiverId,
            viewModelScope
        ).map {
            it.copy(
                imageUrl = if (it.imageUrl.isEmpty()) {
                    it.imageUrl
                } else {
                    getImagePathForFileNameFromLocalDataSource(it.imageUrl)
                }
            )
        }.toUiState()

    private val _manageChangesUiState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val manageChangesUiState: StateFlow<UiState<Unit>> = _manageChangesUiState.asStateFlow()

    fun saveNonHumanAnimalChanges(
        isDifferentImage: Boolean,
        modifiedNonHumanAnimal: NonHumanAnimal
    ) {
        viewModelScope.launch {
            _manageChangesUiState.value = UiState.Loading()

            if (isDifferentImage) {

                deleteCurrentImageFromRemoteDataSource(
                    modifiedNonHumanAnimal.caregiverId,
                    modifiedNonHumanAnimal.id
                ) {
                    deleteCurrentImageFromLocalDataSource(modifiedNonHumanAnimal.id) {

                        uploadNewImageToRemoteDataSource(modifiedNonHumanAnimal) { nonHumanAnimalWithPossibleImageDownloadUri: NonHumanAnimal ->

                            modifyNonHumanAnimalInRemoteDataSource(
                                nonHumanAnimalWithPossibleImageDownloadUri
                            ) {
                                modifyNonHumanAnimalInLocalDataSource(modifiedNonHumanAnimal) {

                                    modifyCacheForNonHumanAnimalInLocalDataSource(
                                        modifiedNonHumanAnimal
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                getNonHumanAnimalFromRemoteRepository(
                    modifiedNonHumanAnimal.id,
                    modifiedNonHumanAnimal.caregiverId
                ).collect { collectedNonHumanAnimal: NonHumanAnimal? ->

                    if (collectedNonHumanAnimal == null) {
                        return@collect
                    }
                    modifyNonHumanAnimalInRemoteDataSource(
                        modifiedNonHumanAnimal.copy(imageUrl = collectedNonHumanAnimal.imageUrl)
                    ) {
                        modifyNonHumanAnimalInLocalDataSource(modifiedNonHumanAnimal) {

                            modifyCacheForNonHumanAnimalInLocalDataSource(modifiedNonHumanAnimal)
                        }
                    }
                }
            }
        }
    }


    private fun deleteCurrentImageFromRemoteDataSource(
        caregiverId: String,
        nonHumanAnimalId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            getNonHumanAnimalFromRemoteRepository(
                nonHumanAnimalId,
                caregiverId
            ).collect { remoteNonHumanAnimal ->

                if (remoteNonHumanAnimal == null) {
                    return@collect
                }
                deleteImageFromRemoteDataSource(
                    userUid = caregiverId,
                    extraId = nonHumanAnimalId,
                    section = Section.NON_HUMAN_ANIMALS,
                    currentImage = remoteNonHumanAnimal.imageUrl
                ) { isDeleted ->

                    if (isDeleted) {
                        log.d(
                            "ModifyNonHumanAnimalViewModel",
                            "deleteCurrentImageFromRemoteDataSource: Image from the non human animal $nonHumanAnimalId was deleted successfully in remote data source"
                        )
                        onSuccess()
                    } else {
                        log.e(
                            "ModifyNonHumanAnimalViewModel",
                            "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the non human animal $nonHumanAnimalId in remote data source"
                        )
                        _manageChangesUiState.value = UiState.Error()
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        nonHumanAnimalId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val localNonHumanAnimal: NonHumanAnimal =
                getNonHumanAnimalFromLocalRepository(nonHumanAnimalId)!!

            deleteImageFromLocalDataSource(currentImagePath = localNonHumanAnimal.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "deleteCurrentImageFromLocalDataSource: Image from the non human animal $nonHumanAnimalId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyNonHumanAnimalViewModel",
                        "deleteCurrentImageFromLocalDataSource: Failed to delete the image from the non human animal $nonHumanAnimalId in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        modifiedNonHumanAnimal: NonHumanAnimal,
        onSuccess: (NonHumanAnimal) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = modifiedNonHumanAnimal.caregiverId,
            extraId = modifiedNonHumanAnimal.id,
            section = Section.NON_HUMAN_ANIMALS,
            imageUri = modifiedNonHumanAnimal.imageUrl
        ) { imageDownloadUri: String ->

            val nonHumanAnimalWithPossibleImageDownloadUri: NonHumanAnimal =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the non human animal ${modifiedNonHumanAnimal.id} is blank"
                    )
                    modifiedNonHumanAnimal
                } else {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the non human animal ${modifiedNonHumanAnimal.id} was saved successfully"
                    )
                    modifiedNonHumanAnimal.copy(imageUrl = imageDownloadUri)
                }
            onSuccess(nonHumanAnimalWithPossibleImageDownloadUri)
        }
    }

    private fun modifyNonHumanAnimalInRemoteDataSource(
        nonHumanAnimal: NonHumanAnimal,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            modifyNonHumanAnimalInRemoteRepository(nonHumanAnimal) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "modifyNonHumanAnimalInRemoteDataSource: non human animal ${nonHumanAnimal.id} updated successfully in remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyNonHumanAnimalViewModel",
                        "modifyNonHumanAnimalInRemoteDataSource: failed to update the non human animal ${nonHumanAnimal.id} in remote data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun modifyNonHumanAnimalInLocalDataSource(
        modifiedNonHumanAnimal: NonHumanAnimal,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            modifyNonHumanAnimalInLocalRepository(modifiedNonHumanAnimal) { rowsModified: Int ->

                if (rowsModified > 0) {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "modifyNonHumanAnimalInLocalDataSource: non human animal ${modifiedNonHumanAnimal.id} updated successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyNonHumanAnimalViewModel",
                        "modifyNonHumanAnimalInLocalDataSource: failed to update the non human animal ${modifiedNonHumanAnimal.id} in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun modifyCacheForNonHumanAnimalInLocalDataSource(nonHumanAnimal: NonHumanAnimal) {

        viewModelScope.launch {

            modifyCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = nonHumanAnimal.id,
                    savedBy = nonHumanAnimal.caregiverId,
                    section = Section.NON_HUMAN_ANIMALS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowsUpdated: Int ->

                if (rowsUpdated > 0) {
                    log.d(
                        "ModifyNonHumanAnimalViewModel",
                        "${nonHumanAnimal.id} updated in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                } else {
                    log.e(
                        "ModifyNonHumanAnimalViewModel",
                        "Error updating ${nonHumanAnimal.id} in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                }
                _manageChangesUiState.value = UiState.Success(Unit)
            }
        }
    }

    fun deleteNonHumanAnimal(id: String, caregiverId: String) {

        _manageChangesUiState.value = UiState.Loading()

        deleteNonHumanAnimalUtil.deleteNonHumanAnimal(
            id = id,
            caregiverId = caregiverId,
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
