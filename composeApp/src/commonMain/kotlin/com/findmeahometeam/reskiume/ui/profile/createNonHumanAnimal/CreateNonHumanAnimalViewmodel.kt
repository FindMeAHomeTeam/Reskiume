package com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateNonHumanAnimalViewmodel(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val uploadImageToRemoteDataSource: UploadImageToRemoteDataSource,
    private val insertNonHumanAnimalInRemoteRepository: InsertNonHumanAnimalInRemoteRepository,
    private val insertNonHumanAnimalInLocalRepository: InsertNonHumanAnimalInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val log: Log
) : ViewModel() {
    private val _saveChangesUiState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val saveChangesUiState: StateFlow<UiState<Unit>> = _saveChangesUiState.asStateFlow()

    @OptIn(ExperimentalTime::class)
    fun saveNonHumanAnimalChanges(nonHumanAnimal: NonHumanAnimal) {

        viewModelScope.launch {
            _saveChangesUiState.value = UiState.Loading()

            val caregiverId: String = observeAuthStateInAuthDataSource().firstOrNull()?.uid!!

            val createdNonHumanAnimal: NonHumanAnimal = nonHumanAnimal.copy(
                id = Clock.System.now().epochSeconds.toString() + caregiverId,
                caregiverId = caregiverId
            )
            uploadNewImageToRemoteDataSource(createdNonHumanAnimal) { nonHumanAnimalWithPossibleImageDownloadUri: NonHumanAnimal ->

                createNonHumanAnimalInRemoteDataSource(
                    nonHumanAnimalWithPossibleImageDownloadUri
                ) {
                    createNonHumanAnimalInLocalDataSource(createdNonHumanAnimal) {

                        createCacheForNonHumanAnimalInLocalDataSource(
                            createdNonHumanAnimal
                        )
                    }
                }
            }
        }
    }

    private fun uploadNewImageToRemoteDataSource(
        createdNonHumanAnimal: NonHumanAnimal,
        onSuccess: (NonHumanAnimal) -> Unit
    ) {
        uploadImageToRemoteDataSource(
            userUid = createdNonHumanAnimal.caregiverId,
            extraId = createdNonHumanAnimal.id,
            section = Section.NON_HUMAN_ANIMALS,
            imageUri = createdNonHumanAnimal.imageUrl
        ) { imageDownloadUri: String ->

            val nonHumanAnimalWithPossibleImageDownloadUri: NonHumanAnimal =
                if (imageDownloadUri.isBlank()) {
                    log.d(
                        "CreateNonHumanAnimalViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the non human animal ${createdNonHumanAnimal.id} is blank"
                    )
                    createdNonHumanAnimal
                } else {
                    log.d(
                        "CreateNonHumanAnimalViewModel",
                        "uploadNewImageToRemoteDataSource: the download URI from the non human animal ${createdNonHumanAnimal.id} was saved successfully"
                    )
                    createdNonHumanAnimal.copy(imageUrl = imageDownloadUri)
                }
            onSuccess(nonHumanAnimalWithPossibleImageDownloadUri)
        }
    }

    private fun createNonHumanAnimalInRemoteDataSource(
        createdNonHumanAnimal: NonHumanAnimal,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            insertNonHumanAnimalInRemoteRepository(createdNonHumanAnimal) { result ->

                if (result is DatabaseResult.Success) {
                    log.d(
                        "CreateNonHumanAnimalViewModel",
                        "createNonHumanAnimalInRemoteDataSource: non human animal ${createdNonHumanAnimal.id} created successfully in remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateNonHumanAnimalViewModel",
                        "createNonHumanAnimalInRemoteDataSource: failed to create the non human animal ${createdNonHumanAnimal.id} in remote data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    private fun createNonHumanAnimalInLocalDataSource(
        createdNonHumanAnimal: NonHumanAnimal,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            insertNonHumanAnimalInLocalRepository(createdNonHumanAnimal) { rowId: Long ->

                if (rowId > 0) {
                    log.d(
                        "CreateNonHumanAnimalViewModel",
                        "createNonHumanAnimalInLocalDataSource: non human animal ${createdNonHumanAnimal.id} created successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateNonHumanAnimalViewModel",
                        "createNonHumanAnimalInLocalDataSource: failed to create the non human animal ${createdNonHumanAnimal.id} in the local data source"
                    )
                    _saveChangesUiState.value = UiState.Error()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createCacheForNonHumanAnimalInLocalDataSource(nonHumanAnimal: NonHumanAnimal) {

        viewModelScope.launch {

            insertCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = nonHumanAnimal.id,
                    savedBy = nonHumanAnimal.caregiverId,
                    section = Section.NON_HUMAN_ANIMALS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId: Long ->

                if (rowId > 0) {
                    log.d(
                        "CreateNonHumanAnimalViewModel",
                        "${nonHumanAnimal.id} created in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                } else {
                    log.e(
                        "CreateNonHumanAnimalViewModel",
                        "Error creating ${nonHumanAnimal.id} in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                }
                _saveChangesUiState.value = UiState.Success(Unit)
            }
        }
    }
}
