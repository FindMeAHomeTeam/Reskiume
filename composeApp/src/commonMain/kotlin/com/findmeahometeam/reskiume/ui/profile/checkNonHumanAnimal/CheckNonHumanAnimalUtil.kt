package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CheckNonHumanAnimalUtil(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getNonHumanAnimalFromRemoteRepository: GetNonHumanAnimalFromRemoteRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertNonHumanAnimalInLocalRepository: InsertNonHumanAnimalInLocalRepository,
    private val modifyNonHumanAnimalInLocalRepository: ModifyNonHumanAnimalInLocalRepository,
    private val getNonHumanAnimalFromLocalRepository: GetNonHumanAnimalFromLocalRepository,
    private val getCompleteImagePathFromLocalDataSource: GetCompleteImagePathFromLocalDataSource,
    private val log: Log
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNonHumanAnimalFlow(
        coroutineScope: CoroutineScope,
        nonHumanAnimalId: String,
        caregiverId: String
    ): Flow<UiState<NonHumanAnimal>> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            getDataByManagingObjectLocalCacheTimestamp<Flow<UiState<NonHumanAnimal>>>(
                cachedObjectId = nonHumanAnimalId,
                savedBy = authUser?.uid ?: "",
                section = Section.NON_HUMAN_ANIMALS,
                onCompletionInsertCache = {
                    getNonHumanAnimalFromRemoteRepository(
                        nonHumanAnimalId,
                        caregiverId
                    ).downloadImageAndInsertNonHumanAnimalInLocalRepository(coroutineScope)
                        .map {
                            if (it == null) {
                                deleteNonHumanAnimalCacheFromLocalDataSource(
                                    nonHumanAnimalId,
                                    coroutineScope
                                )
                                UiState.Error()
                            } else {
                                UiState.Success(it)
                            }
                        }
                },
                onCompletionUpdateCache = {
                    getNonHumanAnimalFromRemoteRepository(
                        nonHumanAnimalId,
                        caregiverId
                    ).downloadImageAndModifyNonHumanAnimalInLocalRepository(coroutineScope)
                        .map {
                            if (it == null) {
                                deleteNonHumanAnimalCacheFromLocalDataSource(
                                    nonHumanAnimalId,
                                    coroutineScope
                                )
                                UiState.Error()
                            } else {
                                UiState.Success(it)
                            }
                        }
                },
                onVerifyCacheIsRecent = {
                    val nonHumanAnimal: NonHumanAnimal? = getNonHumanAnimalFromLocalRepository(
                        nonHumanAnimalId
                    )
                    if (nonHumanAnimal == null) {
                        deleteNonHumanAnimalCacheFromLocalDataSource(
                            nonHumanAnimalId,
                            coroutineScope
                        )
                        flowOf(UiState.Error())
                    } else {
                        flowOf(UiState.Success(nonHumanAnimal))
                    }
                }
            ).map {
                if (it is UiState.Success) {
                    UiState.Success(
                        it.data.copy(
                            imageUrl = getCompleteImagePathFromLocalDataSource(it.data.imageUrl)
                        )
                    )
                } else {
                    it
                }
            }
        }

    private fun Flow<NonHumanAnimal?>.downloadImageAndInsertNonHumanAnimalInLocalRepository(
        coroutineScope: CoroutineScope
    ): Flow<NonHumanAnimal?> =
        this.map { nonHumanAnimal: NonHumanAnimal? ->

            when {
                nonHumanAnimal == null -> { /* do nothing */
                }

                nonHumanAnimal.imageUrl.isBlank() -> {
                    log.d(
                        "CheckNonHumanAnimalViewmodel",
                        "Non human animal ${nonHumanAnimal.id} has no avatar image to save locally."
                    )
                    insertNonHumanAnimalsInLocalRepository(nonHumanAnimal)
                }

                else -> {
                    downloadImageToLocalDataSource(
                        userUid = nonHumanAnimal.caregiverId,
                        extraId = nonHumanAnimal.id,
                        section = Section.NON_HUMAN_ANIMALS
                    ) { localImagePath: String ->

                        val nonHumanAnimalWithLocalImage =
                            nonHumanAnimal.copy(imageUrl = localImagePath.ifBlank { nonHumanAnimal.imageUrl })

                        coroutineScope.launch {

                            insertNonHumanAnimalsInLocalRepository(nonHumanAnimalWithLocalImage)
                        }
                    }
                }
            }
            nonHumanAnimal
        }

    private fun deleteNonHumanAnimalCacheFromLocalDataSource(
        id: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {

            deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "CheckNonHumanAnimalUtil",
                        "Non human animal $id deleted in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                } else {
                    log.e(
                        "CheckNonHumanAnimalUtil",
                        "Error deleting the non human animal $id in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                }
            }
        }
    }

    private suspend fun insertNonHumanAnimalsInLocalRepository(nonHumanAnimal: NonHumanAnimal) {

        insertNonHumanAnimalInLocalRepository(nonHumanAnimal) {
            if (it > 0) {
                log.d(
                    "CheckNonHumanAnimalsViewmodel",
                    "Non human animal ${nonHumanAnimal.id} added to local database"
                )
            } else {
                log.e(
                    "CheckNonHumanAnimalsViewmodel",
                    "Error adding the non human animal ${nonHumanAnimal.id} to local database"
                )
            }
        }
    }

    private fun Flow<NonHumanAnimal?>.downloadImageAndModifyNonHumanAnimalInLocalRepository(
        coroutineScope: CoroutineScope
    ): Flow<NonHumanAnimal?> =
        this.map { nonHumanAnimal: NonHumanAnimal? ->

            when {
                nonHumanAnimal == null -> { /* do nothing */
                }

                nonHumanAnimal.imageUrl.isBlank() -> {
                    log.d(
                        "CheckNonHumanAnimalViewmodel",
                        "Non human animal ${nonHumanAnimal.id} has no avatar image to save locally."
                    )
                    modifyNonHumanAnimalsInLocalRepository(nonHumanAnimal)
                }

                else -> {
                    downloadImageToLocalDataSource(
                        userUid = nonHumanAnimal.caregiverId,
                        extraId = nonHumanAnimal.id,
                        section = Section.NON_HUMAN_ANIMALS
                    ) { localImagePath: String ->

                        val nonHumanAnimalWithLocalImage =
                            nonHumanAnimal.copy(imageUrl = localImagePath.ifBlank { nonHumanAnimal.imageUrl })

                        coroutineScope.launch {

                            modifyNonHumanAnimalsInLocalRepository(nonHumanAnimalWithLocalImage)
                        }
                    }
                }
            }
            nonHumanAnimal
        }

    private suspend fun modifyNonHumanAnimalsInLocalRepository(nonHumanAnimal: NonHumanAnimal) {

        modifyNonHumanAnimalInLocalRepository(nonHumanAnimal) {
            if (it > 0) {
                log.d(
                    "CheckNonHumanAnimalsViewmodel",
                    "Non human animal ${nonHumanAnimal.id} modified in local database"
                )
            } else {
                log.e(
                    "CheckNonHumanAnimalsViewmodel",
                    "Error modifying the non human animal ${nonHumanAnimal.id} in local database"
                )
            }
        }
    }
}
