package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
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

class ModifyFosterHomeViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val getFosterHomeFromRemoteRepository: GetFosterHomeFromRemoteRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val modifyFosterHomeInRemoteRepository: ModifyFosterHomeInRemoteRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val deleteFosterHomeUtil: DeleteFosterHomeUtil,
    private val log: Log
) : ViewModel() {

    private val fosterHomeId: String =
        saveStateHandleProvider.provideObjectRoute(ModifyFosterHome::class).fosterHomeId

    private val ownerId: String =
        saveStateHandleProvider.provideObjectRoute(ModifyFosterHome::class).ownerId

    val fosterHomeFlow: Flow<UiState<FosterHome>> =
        getFosterHomeFromLocalRepository(
            fosterHomeId
        ).map { fosterHome: FosterHome? ->
            fosterHome!!.copy(
                imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                    fosterHome.imageUrl
                } else {
                    getImagePathForFileNameFromLocalDataSource(fosterHome.imageUrl)
                }
            )
        }.toUiState()

    val allAvailableNonHumanAnimalsFlow: Flow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository(ownerId).map {
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

    fun saveFosterHomeChanges(
        isDifferentImage: Boolean,
        modifiedFosterHome: FosterHome
    ) {
        viewModelScope.launch {
            _manageChangesUiState.value = UiState.Loading()

            if (isDifferentImage) {

                deleteCurrentImageFromRemoteDataSource(
                    modifiedFosterHome.ownerId,
                    modifiedFosterHome.id
                ) {
                    deleteCurrentImageFromLocalDataSource(modifiedFosterHome.id) {

                        uploadNewImageToRemoteDataSource(modifiedFosterHome) { fosterHomeWithPossibleImageDownloadUri: FosterHome ->

                            modifyFosterHomeInRemoteDataSource(
                                fosterHomeWithPossibleImageDownloadUri
                            ) {
                                modifyFosterHomeInLocalDataSource(modifiedFosterHome) {

                                    modifyCacheForFosterHomeInLocalDataSource(
                                        modifiedFosterHome
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val collectedFosterHome = getFosterHomeFromRemoteRepository(
                    modifiedFosterHome.id
                ).first()

                modifyFosterHomeInRemoteDataSource(
                    modifiedFosterHome.copy(imageUrl = collectedFosterHome.imageUrl)
                ) {
                    modifyFosterHomeInLocalDataSource(modifiedFosterHome) {

                        modifyCacheForFosterHomeInLocalDataSource(modifiedFosterHome)
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromRemoteDataSource(
        ownerId: String,
        fosterHomeId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val remoteFosterHome = getFosterHomeFromRemoteRepository(
                fosterHomeId
            ).first()

            deleteImageFromRemoteDataSource(
                userUid = ownerId,
                extraId = fosterHomeId,
                section = Section.FOSTER_HOMES,
                currentImage = remoteFosterHome.imageUrl
            ) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "deleteCurrentImageFromRemoteDataSource: Image from the foster home $fosterHomeId was deleted successfully in remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyFosterHomeViewModel",
                        "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the foster home $fosterHomeId in remote data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        fosterHomeId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val localFosterHome: FosterHome? =
                getFosterHomeFromLocalRepository(
                    fosterHomeId
                ).firstOrNull()

            if (localFosterHome == null) {
                log.e(
                    "ModifyFosterHomeViewModel",
                    "deleteCurrentImageFromLocalDataSource: Failed to delete the image from the foster home $fosterHomeId in the local data source because the local foster home does not exist!"
                )
                _manageChangesUiState.value = UiState.Error()
                return@launch
            }

            deleteImageFromLocalDataSource(currentImagePath = localFosterHome.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "deleteCurrentImageFromLocalDataSource: Image from the foster home $fosterHomeId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyFosterHomeViewModel",
                        "deleteCurrentImageFromLocalDataSource: Failed to delete the image from the foster home $fosterHomeId in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        modifiedFosterHome: FosterHome,
        onComplete: (FosterHome) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = modifiedFosterHome.ownerId,
            extraId = modifiedFosterHome.id,
            section = Section.FOSTER_HOMES,
            imageUri = modifiedFosterHome.imageUrl
        ) { imageDownloadUri: String ->

            val fosterHomeWithPossibleImageDownloadUri: FosterHome =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the foster home ${modifiedFosterHome.id} is blank"
                    )
                    modifiedFosterHome
                } else {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the foster home ${modifiedFosterHome.id} was saved successfully"
                    )
                    modifiedFosterHome.copy(imageUrl = imageDownloadUri)
                }
            onComplete(fosterHomeWithPossibleImageDownloadUri)
        }
    }

    private fun modifyFosterHomeInRemoteDataSource(
        updatedFosterHome: FosterHome,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val previousFosterHome =
                getFosterHomeFromLocalRepository(updatedFosterHome.id).first()!!

            modifyFosterHomeInRemoteRepository(
                updatedFosterHome,
                previousFosterHome
            ) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "modifyFosterHomeInRemoteDataSource: foster home ${updatedFosterHome.id} updated successfully in remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyFosterHomeViewModel",
                        "modifyFosterHomeInRemoteDataSource: failed to update the foster home ${updatedFosterHome.id} in remote data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun modifyFosterHomeInLocalDataSource(
        updatedFosterHome: FosterHome,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            val previousFosterHome =
                getFosterHomeFromLocalRepository(updatedFosterHome.id).first()!!

            modifyFosterHomeInLocalRepository(
                updatedFosterHome,
                previousFosterHome
            ) { isUpdated: Boolean ->

                if (isUpdated) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "modifyFosterHomeInLocalDataSource: foster home ${updatedFosterHome.id} updated successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "ModifyFosterHomeViewModel",
                        "modifyFosterHomeInLocalDataSource: failed to update the foster home ${updatedFosterHome.id} in the local data source"
                    )
                    _manageChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun modifyCacheForFosterHomeInLocalDataSource(fosterHome: FosterHome) {

        viewModelScope.launch {

            modifyCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = fosterHome.id,
                    savedBy = fosterHome.ownerId,
                    section = Section.FOSTER_HOMES,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowsUpdated: Int ->

                if (rowsUpdated > 0) {
                    log.d(
                        "ModifyFosterHomeViewModel",
                        "modifyCacheForFosterHomeInLocalDataSource: ${fosterHome.id} updated in local cache in section ${Section.FOSTER_HOMES}"
                    )
                } else {
                    log.e(
                        "ModifyFosterHomeViewModel",
                        "modifyCacheForFosterHomeInLocalDataSource: Error updating ${fosterHome.id} in local cache in section ${Section.FOSTER_HOMES}"
                    )
                }
                _manageChangesUiState.value = UiState.Success(Unit)
            }
        }
    }

    fun deleteFosterHome(id: String, ownerId: String) {

        _manageChangesUiState.value = UiState.Loading()

        deleteFosterHomeUtil.deleteFosterHome(
            id = id,
            ownerId = ownerId,
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
