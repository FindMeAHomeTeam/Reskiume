package com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteNonHumanAnimalUtil(
    private val getNonHumanAnimalFromRemoteRepository: GetNonHumanAnimalFromRemoteRepository,
    private val getNonHumanAnimalFromLocalRepository: GetNonHumanAnimalFromLocalRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteNonHumanAnimalFromRemoteRepository: DeleteNonHumanAnimalFromRemoteRepository,
    private val deleteNonHumanAnimalFromLocalRepository: DeleteNonHumanAnimalFromLocalRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val log: Log
) {

    fun deleteNonHumanAnimal(
        id: String,
        caregiverId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean = false, // In case the user is not owner of the remote data
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        deleteCurrentImageFromRemoteDataSource(
            caregiverId,
            id,
            coroutineScope,
            onlyDeleteOnLocal,
            onError
        ) {
            deleteCurrentImageFromLocalDataSource(
                id,
                coroutineScope,
                onError
            ) {
                deleteNonHumanAnimalFromRemoteDataSource(
                    id,
                    caregiverId,
                    coroutineScope,
                    onlyDeleteOnLocal,
                    onError
                ) {
                    deleteNonHumanAnimalFromLocalDataSource(
                        id,
                        coroutineScope,
                        onError
                    ) {
                        deleteNonHumanAnimalCacheFromLocalDataSource(
                            id,
                            coroutineScope,
                            onComplete
                        )
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromRemoteDataSource(
        caregiverId: String,
        nonHumanAnimalId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (onlyDeleteOnLocal) {
            onSuccess()
            return
        }
        coroutineScope.launch {

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
                            "DeleteNonHumanAnimalUtil",
                            "deleteCurrentImageFromRemoteDataSource: Image from the non human animal $nonHumanAnimalId was deleted successfully in the remote data source"
                        )
                        onSuccess()
                    } else {
                        log.e(
                            "DeleteNonHumanAnimalUtil",
                            "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the non human animal $nonHumanAnimalId in the remote data source"
                        )
                        onError()
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        nonHumanAnimalId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {

            val localNonHumanAnimal: NonHumanAnimal =
                getNonHumanAnimalFromLocalRepository(nonHumanAnimalId)!!

            deleteImageFromLocalDataSource(currentImagePath = localNonHumanAnimal.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "DeleteNonHumanAnimalUtil",
                        "deleteCurrentImageFromLocalDataSource: Image from the non human animal $nonHumanAnimalId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteNonHumanAnimalUtil",
                        "deleteCurrentImageFromLocalDataSource: Failed to delete the image from the non human animal $nonHumanAnimalId in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteNonHumanAnimalFromRemoteDataSource(
        id: String,
        caregiverId: String,
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (onlyDeleteOnLocal) {
            onSuccess()
            return
        }
        coroutineScope.launch {

            deleteNonHumanAnimalFromRemoteRepository(
                id,
                caregiverId
            ) { databaseResult: DatabaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "DeleteNonHumanAnimalUtil",
                        "deleteNonHumanAnimalFromRemoteDataSource: Non human animal $id deleted in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteNonHumanAnimalUtil",
                        "deleteNonHumanAnimalFromRemoteDataSource: Error deleting the non human animal $id in the remote data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteNonHumanAnimalFromLocalDataSource(
        id: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {

            deleteNonHumanAnimalFromLocalRepository(id) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteNonHumanAnimalUtil",
                        "deleteNonHumanAnimalFromLocalDataSource: Non human animal $id deleted in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteNonHumanAnimalUtil",
                        "deleteNonHumanAnimalFromLocalDataSource: Error deleting the non human animal $id in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteNonHumanAnimalCacheFromLocalDataSource(
        id: String,
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit
    ) {
        coroutineScope.launch {

            deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteNonHumanAnimalUtil",
                        "Non human animal $id deleted in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                } else {
                    log.e(
                        "DeleteNonHumanAnimalUtil",
                        "Error deleting the non human animal $id in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                    )
                }
                onComplete()
            }
        }
    }
}
