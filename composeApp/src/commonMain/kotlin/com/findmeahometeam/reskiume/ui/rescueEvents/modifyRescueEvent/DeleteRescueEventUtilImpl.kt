package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DeleteRescueEventUtilImpl(
    private val getRescueEventFromRemoteRepository: GetRescueEventFromRemoteRepository,
    private val getRescueEventFromLocalRepository: GetRescueEventFromLocalRepository,
    private val deleteImageFromRemoteDataSource: DeleteImageFromRemoteDataSource,
    private val deleteImageFromLocalDataSource: DeleteImageFromLocalDataSource,
    private val deleteMyRescueEventFromRemoteRepository: DeleteMyRescueEventFromRemoteRepository,
    private val deleteMyRescueEventFromLocalRepository: DeleteMyRescueEventFromLocalRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val log: Log
) : DeleteRescueEventUtil {

    override fun deleteRescueEvent(
        id: String,
        creatorId: String,
        nonHumanAnimalState: NonHumanAnimalState,
        coroutineScope: CoroutineScope,
        deleteOnLocal: Boolean,
        deleteOnRemote: Boolean,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        deleteCurrentImageFromRemoteDataSource(
            creatorId,
            id,
            deleteOnRemote,
            coroutineScope,
            onError
        ) {
            deleteCurrentImageFromLocalDataSource(
                id,
                deleteOnLocal,
                coroutineScope,
                onError
            ) {
                deleteRescueEventFromRemoteDataSource(
                    id,
                    nonHumanAnimalState,
                    deleteOnRemote,
                    coroutineScope,
                    onError
                ) {
                    deleteRescueEventFromLocalDataSource(
                        id,
                        nonHumanAnimalState,
                        deleteOnLocal,
                        coroutineScope,
                        onError
                    ) {
                        deleteRescueEventCacheFromLocalDataSource(
                            id,
                            deleteOnLocal,
                            onComplete
                        )
                    }
                }
            }
        }
    }

    private fun deleteCurrentImageFromRemoteDataSource(
        creatorId: String,
        rescueEventId: String,
        deleteOnRemote: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (!deleteOnRemote) {
            onSuccess()
            return
        }
        coroutineScope.launch {

            val remoteRescueEvent = getRescueEventFromRemoteRepository(
                rescueEventId
            ).firstOrNull()

            if (remoteRescueEvent == null) {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event $rescueEventId in the remote data source because the remote rescue event does not exist!"
                )
                onError()
                return@launch
            }

            deleteImageFromRemoteDataSource(
                userUid = creatorId,
                extraId = rescueEventId,
                section = Section.RESCUE_EVENTS,
                currentImage = remoteRescueEvent.imageUrl
            ) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "DeleteRescueEventUtil",
                        "deleteCurrentImageFromRemoteDataSource: Image from the rescue event $rescueEventId was deleted successfully in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteRescueEventUtil",
                        "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event $rescueEventId in the remote data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteCurrentImageFromLocalDataSource(
        rescueEventId: String,
        deleteOnLocal: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (!deleteOnLocal) {
            onSuccess()
            return
        }
        coroutineScope.launch {

            val localRescueEvent: RescueEvent? =
                getRescueEventFromLocalRepository(rescueEventId).firstOrNull()

            if (localRescueEvent == null) {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event $rescueEventId in the local data source because the local rescue event does not exist"
                )
                onError()
                return@launch
            }
            deleteImageFromLocalDataSource(currentImagePath = localRescueEvent.imageUrl) { isDeleted ->

                if (isDeleted) {
                    log.d(
                        "DeleteRescueEventUtil",
                        "deleteCurrentImageFromLocalDataSource: Image from the rescue event $rescueEventId was deleted successfully in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteRescueEventUtil",
                        "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event $rescueEventId in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteRescueEventFromRemoteDataSource(
        id: String,
        nonHumanAnimalState: NonHumanAnimalState,
        deleteOnRemote: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (!deleteOnRemote) {
            onSuccess()
            return
        }
        coroutineScope.launch {

            deleteMyRescueEventFromRemoteRepository(
                id,
                nonHumanAnimalState,
                coroutineScope
            ) { databaseResult: DatabaseResult ->

                if (databaseResult is DatabaseResult.Success) {
                    log.d(
                        "DeleteRescueEventUtil",
                        "deleteRescueEventFromRemoteDataSource: Rescue event $id deleted in the remote data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteRescueEventUtil",
                        "deleteRescueEventFromRemoteDataSource: Error deleting the rescue event $id in the remote data source"
                    )
                    onError()
                }
            }
        }
    }

    private fun deleteRescueEventFromLocalDataSource(
        id: String,
        nonHumanAnimalState: NonHumanAnimalState,
        deleteOnLocal: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: suspend () -> Unit
    ) {
        coroutineScope.launch {

            if (!deleteOnLocal) {
                onSuccess()
                return@launch
            }
            deleteMyRescueEventFromLocalRepository(
                id,
                nonHumanAnimalState,
                coroutineScope
            ) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "DeleteRescueEventUtil",
                        "deleteRescueEventFromLocalDataSource: Rescue event $id deleted in the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "DeleteRescueEventUtil",
                        "deleteRescueEventFromLocalDataSource: Error deleting the rescue event $id in the local data source"
                    )
                    onError()
                }
            }
        }
    }

    private suspend fun deleteRescueEventCacheFromLocalDataSource(
        id: String,
        deleteOnLocal: Boolean,
        onComplete: () -> Unit
    ) {
        if (!deleteOnLocal) {
            onComplete()
            return
        }
        deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

            if (rowsDeleted > 0) {
                log.d(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Rescue event $id deleted in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            } else {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Error deleting the rescue event $id in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
            onComplete()
        }
    }
}
