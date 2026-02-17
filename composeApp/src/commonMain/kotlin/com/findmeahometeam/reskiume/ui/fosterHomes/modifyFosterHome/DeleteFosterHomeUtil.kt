package com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DeleteFosterHomeUtil(
    private val getFosterHomeFromRemoteRepository: GetFosterHomeFromRemoteRepository,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteFosterHomeFromRemoteRepository: DeleteFosterHomeFromRemoteRepository,
    private val deleteFosterHomeFromLocalRepository: DeleteFosterHomeFromLocalRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val log: Log
) {
    fun deleteFosterHome(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        deleteCurrentImageFromRemoteDataSource(
            ownerId,
            id,
            coroutineScope,
            onError
        ) {
            deleteCurrentImageFromLocalDataSource(
                id,
                coroutineScope,
                onError
            ) {
                deleteFosterHomeFromRemoteDataSource(
                    id,
                    ownerId,
                    coroutineScope,
                    onError
                ) {
                    deleteFosterHomeFromLocalDataSource(
                        id,
                        coroutineScope,
                        onError
                    ) {
                        deleteFosterHomeCacheFromLocalDataSource(
                            id,
                            onComplete
                        )
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromRemoteDataSource(
        ownerId: String,
        fosterHomeId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {

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
                        "DeleteFosterHomeUtil",
                        "deleteCurrentImageFromRemoteDataSource: Image from the foster home $fosterHomeId was deleted successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteFosterHomeUtil",
                        "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the foster home $fosterHomeId in the remote data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        fosterHomeId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {

            val localFosterHome: FosterHome? =
                getFosterHomeFromLocalRepository(fosterHomeId).firstOrNull()

            if (localFosterHome == null) {
                onError()
                return@launch
            }
            deleteImageFromLocalDataSource(currentImagePath = localFosterHome.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "DeleteFosterHomeUtil",
                        "deleteCurrentImageFromLocalDataSource: Image from the foster home $fosterHomeId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteFosterHomeUtil",
                        "deleteCurrentImageFromLocalDataSource: Failed to delete the image from the foster home $fosterHomeId in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteFosterHomeFromRemoteDataSource(
        id: String,
        ownerId: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        coroutineScope.launch {

            deleteFosterHomeFromRemoteRepository(
                id,
                ownerId
            ) { databaseResult: DatabaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "DeleteFosterHomeUtil",
                        "deleteFosterHomeFromRemoteDataSource: Foster home $id deleted in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteFosterHomeUtil",
                        "deleteFosterHomeFromRemoteDataSource: Error deleting the foster home $id in the remote data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteFosterHomeFromLocalDataSource(
        id: String,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: suspend () -> Unit
    ) {
        coroutineScope.launch {

            deleteFosterHomeFromLocalRepository(id) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteFosterHomeUtil",
                        "deleteFosterHomeFromLocalDataSource: Foster home $id deleted in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteFosterHomeUtil",
                        "deleteFosterHomeFromLocalDataSource: Error deleting the foster home $id in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private suspend fun deleteFosterHomeCacheFromLocalDataSource(
        id: String,
        onComplete: () -> Unit
    ) {
        deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

            if (rowsDeleted > 0) {
                log.d(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeCacheFromLocalDataSource: Foster home $id deleted in the local cache in section ${Section.FOSTER_HOMES}"
                )
            } else {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeCacheFromLocalDataSource: Error deleting the foster home $id in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
            onComplete()
        }
    }
}
