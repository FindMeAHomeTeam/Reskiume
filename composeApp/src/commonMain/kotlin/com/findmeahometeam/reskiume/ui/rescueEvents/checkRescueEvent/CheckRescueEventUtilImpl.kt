package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class CheckRescueEventUtilImpl(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getRescueEventFromRemoteRepository: GetRescueEventFromRemoteRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertRescueEventInLocalRepository: InsertRescueEventInLocalRepository,
    private val modifyRescueEventInLocalRepository: ModifyRescueEventInLocalRepository,
    private val getRescueEventFromLocalRepository: GetRescueEventFromLocalRepository,
    private val log: Log
) : CheckRescueEventUtil {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRescueEventFlow(
        rescueEventId: String,
        creatorId: String,
        coroutineScope: CoroutineScope
    ): Flow<RescueEvent?> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            val myUid = authUser?.uid ?: " "

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = rescueEventId,
                savedBy = myUid,
                section = Section.RESCUE_EVENTS,
                onCompletionInsertCache = {
                    getRescueEventFromRemoteRepository(
                        rescueEventId
                    ).downloadImageAndInsertRescueEventInLocalRepository(coroutineScope).map {
                        if (it == null) {
                            deleteRescueEventCacheFromLocalDataSource(rescueEventId)
                        }
                        it
                    }
                },
                onCompletionUpdateCache = {
                    getRescueEventFromRemoteRepository(
                        rescueEventId
                    ).downloadImageAndModifyRescueEventInLocalRepository(coroutineScope).map {
                        if (it == null) {
                            deleteRescueEventCacheFromLocalDataSource(rescueEventId)
                        }
                        it
                    }
                },
                onVerifyCacheIsRecent = {
                    getRescueEventFromLocalRepository(rescueEventId).map {
                        if (it == null) {
                            deleteRescueEventCacheFromLocalDataSource(rescueEventId)
                        }
                        it
                    }
                }
            )
        }

    private fun Flow<RescueEvent?>.downloadImageAndInsertRescueEventInLocalRepository(coroutineScope: CoroutineScope): Flow<RescueEvent?> =
        this.map { rescueEvent: RescueEvent? ->

            when {
                rescueEvent == null -> null

                rescueEvent.imageUrl.isBlank() -> {
                    log.d(
                        "CheckRescueEventUtilImpl",
                        "downloadImageAndInsertRescueEventInLocalRepository: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                    )
                    insertRescueEventsInLocalRepository(rescueEvent, coroutineScope)

                    rescueEvent
                }

                else -> {
                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEvent.creatorId,
                        extraId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val rescueEventWithLocalImage = rescueEvent.copy(
                        imageUrl = localImagePath.ifBlank { rescueEvent.imageUrl }
                    )

                    insertRescueEventsInLocalRepository(rescueEventWithLocalImage, coroutineScope)
                    rescueEventWithLocalImage
                }
            }
        }

    private suspend fun deleteRescueEventCacheFromLocalDataSource(
        id: String
    ) {
        deleteCacheFromLocalRepository(id) { rowsDeleted: Int ->

            if (rowsDeleted > 0) {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "deleteRescueEventCacheFromLocalDataSource: Rescue event $id deleted in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            } else {
                log.e(
                    "CheckRescueEventUtilImpl",
                    "deleteRescueEventCacheFromLocalDataSource: Error deleting the Rescue event $id in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
    }

    private suspend fun insertRescueEventsInLocalRepository(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ) {
        insertRescueEventInLocalRepository(
            rescueEvent,
            coroutineScope
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "insertRescueEventsInLocalRepository: Rescue event ${rescueEvent.id} added to local database"
                )
            } else {
                log.e(
                    "CheckRescueEventUtilImpl",
                    "insertRescueEventsInLocalRepository: Error adding the Rescue event ${rescueEvent.id} to local database"
                )
            }
        }
    }

    private fun Flow<RescueEvent?>.downloadImageAndModifyRescueEventInLocalRepository(coroutineScope: CoroutineScope): Flow<RescueEvent?> =
        this.map { rescueEvent: RescueEvent? ->

            when {
                rescueEvent == null -> null

                rescueEvent.imageUrl.isBlank() -> {
                    log.d(
                        "CheckRescueEventUtilImpl",
                        "downloadImageAndModifyRescueEventInLocalRepository: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                    )
                    modifyRescueEventsInLocalRepository(rescueEvent, coroutineScope)

                    rescueEvent
                }

                else -> {
                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEvent.creatorId,
                        extraId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )

                    val rescueEventWithLocalImage = rescueEvent.copy(
                        imageUrl = localImagePath.ifBlank { rescueEvent.imageUrl }
                    )

                    modifyRescueEventsInLocalRepository(rescueEventWithLocalImage, coroutineScope)
                    rescueEventWithLocalImage
                }
            }
        }

    private suspend fun modifyRescueEventsInLocalRepository(
        updatedRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope
    ) {
        val previousRescueEvent =
            getRescueEventFromLocalRepository(updatedRescueEvent.id).first()!!

        modifyRescueEventInLocalRepository(
            updatedRescueEvent,
            previousRescueEvent,
            coroutineScope
        ) { isSuccess ->
            if (isSuccess) {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "modifyRescueEventsInLocalRepository: Rescue event ${updatedRescueEvent.id} modified in local database"
                )
            } else {
                log.e(
                    "CheckRescueEventUtilImpl",
                    "modifyRescueEventsInLocalRepository: Error modifying the Rescue event ${updatedRescueEvent.id} in local database"
                )
            }
        }
    }
}
