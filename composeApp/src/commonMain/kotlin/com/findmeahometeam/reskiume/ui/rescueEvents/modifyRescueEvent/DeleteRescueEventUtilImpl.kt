package com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
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
        coroutineScope: CoroutineScope,
        onlyDeleteOnLocal: Boolean,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        deleteCurrentImageFromRemoteDataSource(
            creatorId,
            id,
            onlyDeleteOnLocal,
            coroutineScope,
            onError
        ) {
            deleteCurrentImageFromLocalDataSource(
                id,
                coroutineScope,
                onError
            ) {
                deleteRescueEventFromRemoteDataSource(
                    id,
                    onlyDeleteOnLocal,
                    coroutineScope,
                    onError
                ) {
                    deleteRescueEventFromLocalDataSource(
                        id,
                        coroutineScope,
                        onError
                    ) {
                        deleteRescueEventCacheFromLocalDataSource(
                            id,
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
        onlyDeleteOnLocal: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (onlyDeleteOnLocal) {
            onSuccess()
            return
        }
        coroutineScope.launch {

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
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
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
        onlyDeleteOnLocal: Boolean,
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (onlyDeleteOnLocal) {
            onSuccess()
            return
        }
        coroutineScope.launch {

            deleteMyRescueEventFromRemoteRepository(
                id,
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
        coroutineScope: CoroutineScope,
        onError: () -> Unit,
        onSuccess: suspend () -> Unit
    ) {
        coroutineScope.launch {

            deleteMyRescueEventFromLocalRepository(
                id,
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
        onComplete: () -> Unit
    ) {
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
