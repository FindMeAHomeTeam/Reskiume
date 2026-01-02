package com.findmeahometeam.reskiume.ui.profile.checkAllNonHumanAnimals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllNonHumanAnimals
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class CheckAllNonHumanAnimalsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllNonHumanAnimalsFromRemoteRepository: GetAllNonHumanAnimalsFromRemoteRepository,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertNonHumanAnimalInLocalRepository: InsertNonHumanAnimalInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyNonHumanAnimalInLocalRepository: ModifyNonHumanAnimalInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val getCompleteImagePathFromLocalDataSource: GetCompleteImagePathFromLocalDataSource,
    private val log: Log
) : ViewModel() {

    private val caregiverId =
        saveStateHandleProvider.provideObjectRoute(CheckAllNonHumanAnimals::class).caregiverId

    val nonHumanAnimalListFlow: Flow<List<NonHumanAnimal>> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = caregiverId,
                savedBy = authUser?.uid ?: "",
                section = Section.NON_HUMAN_ANIMALS,
                onCompletionInsertCache = {
                    getAllNonHumanAnimalsFromRemoteRepository(caregiverId).downloadImageAndInsertNonHumanAnimalsInLocalRepository()
                },
                onCompletionUpdateCache = {
                    getAllNonHumanAnimalsFromRemoteRepository(caregiverId).downloadImageAndModifyNonHumanAnimalsInLocalRepository()
                },
                onVerifyCacheIsRecent = {
                    getAllNonHumanAnimalsFromLocalRepository(caregiverId)
                }
            ).map {
                it.map { nonHumanAnimal ->
                    nonHumanAnimal.copy(
                        imageUrl = getCompleteImagePathFromLocalDataSource(nonHumanAnimal.imageUrl)
                    )
                }
            }
        }

    private fun Flow<List<NonHumanAnimal>>.downloadImageAndInsertNonHumanAnimalsInLocalRepository(): Flow<List<NonHumanAnimal>> =
        this.map { nonHumanAnimalList ->
            nonHumanAnimalList.map { nonHumanAnimal ->

                if (nonHumanAnimal.imageUrl.isNotBlank()) {

                    downloadImageToLocalDataSource(
                        userUid = nonHumanAnimal.caregiverId,
                        extraId = nonHumanAnimal.id,
                        section = Section.NON_HUMAN_ANIMALS
                    ) { localImagePath: String ->

                        val nonHumanAnimalWithLocalImage =
                            nonHumanAnimal.copy(imageUrl = localImagePath.ifBlank { nonHumanAnimal.imageUrl })
                        insertNonHumanAnimalsInLocalRepository(nonHumanAnimalWithLocalImage)
                    }
                } else {
                    log.d(
                        "CheckAllNonHumanAnimalsViewmodel",
                        "Non human animal ${nonHumanAnimal.id} has no avatar image to save locally."
                    )
                    insertNonHumanAnimalsInLocalRepository(nonHumanAnimal)
                }
                nonHumanAnimal
            }
        }

    @OptIn(ExperimentalTime::class)
    private fun insertNonHumanAnimalsInLocalRepository(nonHumanAnimal: NonHumanAnimal) {

        viewModelScope.launch {

            insertNonHumanAnimalInLocalRepository(nonHumanAnimal) {
                if (it > 0) {
                    log.d(
                        "CheckNonHumanAnimalsViewmodel",
                        "Non human animal ${nonHumanAnimal.id} added to local database"
                    )
                    viewModelScope.launch {

                        insertCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = nonHumanAnimal.id,
                                savedBy = caregiverId,
                                section = Section.NON_HUMAN_ANIMALS,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowId ->

                            if (rowId > 0) {
                                log.d(
                                    "CheckAllNonHumanAnimalsViewmodel",
                                    "${nonHumanAnimal.id} added to local cache in section ${Section.NON_HUMAN_ANIMALS}"
                                )
                            } else {
                                log.e(
                                    "CheckAllNonHumanAnimalsViewmodel",
                                    "Error adding ${nonHumanAnimal.id} to local cache in section ${Section.NON_HUMAN_ANIMALS}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckNonHumanAnimalsViewmodel",
                        "Error adding the non human animal ${nonHumanAnimal.id} to local database"
                    )
                }
            }
        }
    }


    private fun Flow<List<NonHumanAnimal>>.downloadImageAndModifyNonHumanAnimalsInLocalRepository(): Flow<List<NonHumanAnimal>> =
        this.map { nonHumanAnimalList ->
            nonHumanAnimalList.map { nonHumanAnimal ->

                if (nonHumanAnimal.imageUrl.isNotBlank()) {

                    downloadImageToLocalDataSource(
                        userUid = nonHumanAnimal.caregiverId,
                        extraId = nonHumanAnimal.id,
                        section = Section.NON_HUMAN_ANIMALS
                    ) { localImagePath: String ->

                        val nonHumanAnimalWithLocalImage =
                            nonHumanAnimal.copy(imageUrl = localImagePath.ifBlank { nonHumanAnimal.imageUrl })
                        modifyNonHumanAnimalsInLocalRepository(nonHumanAnimalWithLocalImage)
                    }
                } else {
                    log.d(
                        "CheckAllNonHumanAnimalsViewmodel",
                        "Non human animal ${nonHumanAnimal.id} has no avatar image to save locally."
                    )
                    modifyNonHumanAnimalsInLocalRepository(nonHumanAnimal)
                }
                nonHumanAnimal
            }
        }

    @OptIn(ExperimentalTime::class)
    private fun modifyNonHumanAnimalsInLocalRepository(nonHumanAnimal: NonHumanAnimal) {

        viewModelScope.launch {

            modifyNonHumanAnimalInLocalRepository(nonHumanAnimal) {
                if (it > 0) {
                    log.d(
                        "CheckNonHumanAnimalsViewmodel",
                        "Non human animal ${nonHumanAnimal.id} modified in local database"
                    )
                    viewModelScope.launch {

                        modifyCacheInLocalRepository(
                            LocalCache(
                                cachedObjectId = nonHumanAnimal.id,
                                savedBy = caregiverId,
                                section = Section.NON_HUMAN_ANIMALS,
                                timestamp = Clock.System.now().epochSeconds
                            )
                        ) { rowsUpdated ->

                            if (rowsUpdated > 0) {
                                log.d(
                                    "CheckAllNonHumanAnimalsViewmodel",
                                    "${nonHumanAnimal.id} updated in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                                )
                            } else {
                                log.e(
                                    "CheckAllNonHumanAnimalsViewmodel",
                                    "Error updating ${nonHumanAnimal.id} in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                                )
                            }
                        }
                    }
                } else {
                    log.e(
                        "CheckNonHumanAnimalsViewmodel",
                        "Error modifying the non human animal ${nonHumanAnimal.id} in local database"
                    )
                }
            }
        }
    }
}
