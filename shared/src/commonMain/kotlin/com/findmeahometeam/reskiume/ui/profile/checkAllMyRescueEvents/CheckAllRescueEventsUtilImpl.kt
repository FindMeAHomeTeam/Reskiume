package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckAllRescueEventsUtilImpl(
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertRescueEventInLocalRepository: InsertRescueEventInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyRescueEventInLocalRepository: ModifyRescueEventInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val deleteRescueEventUtil: DeleteRescueEventUtil,
    private val log: Log
) : CheckAllRescueEventsUtil {

    override suspend fun updateLocalRepositoryWithRemoteRescueEvents(
        allRemoteRescueEvents: Set<RescueEvent>,
        allLocalRescueEvents: Set<RescueEvent>,
        myUid: String,
        coroutineScope: CoroutineScope
    ) {
        val allRescueEventIdsToManage: Set<String> =
            allRemoteRescueEvents.map { it.id } union allLocalRescueEvents.map { it.id }
        allRescueEventIdsToManage.forEach { rescueEventIdToManage ->

            val rescueEventToManage = allRemoteRescueEvents.find { it.id == rescueEventIdToManage }
                ?: allLocalRescueEvents.find { it.id == rescueEventIdToManage }!!

            if (allRemoteRescueEvents.any { it.id == rescueEventToManage.id }) {

                val localRescueEvent: RescueEvent? =
                    allLocalRescueEvents.find { it.id == rescueEventToManage.id }

                if (rescueEventToManage.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEventToManage.creatorId,
                        extraId = rescueEventToManage.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val rescueEventWithLocalImage =
                        rescueEventToManage.copy(imageUrl = localImagePath.ifBlank { rescueEventToManage.imageUrl })

                    if (localRescueEvent == null) {
                        insertRescueEventInLocalRepo(
                            rescueEvent = rescueEventWithLocalImage,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    } else {
                        modifyRescueEventInLocalRepo(
                            updatedRescueEvent = rescueEventWithLocalImage,
                            previousRescueEvent = localRescueEvent,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    }
                } else {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "updateLocalRepositoryWithRemoteRescueEvents: Rescue event ${rescueEventToManage.id} has no avatar image to save locally."
                    )

                    if (localRescueEvent == null) {
                        insertRescueEventInLocalRepo(
                            rescueEvent = rescueEventToManage,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    } else {
                        modifyRescueEventInLocalRepo(
                            updatedRescueEvent = rescueEventToManage,
                            previousRescueEvent = localRescueEvent,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    }
                }
            } else {
                deleteLocalRescueEvent(
                    id = rescueEventToManage.id,
                    creatorId = rescueEventToManage.creatorId,
                    coroutineScope = coroutineScope
                )
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun insertRescueEventInLocalRepo(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        val isSuccess = insertRescueEventInLocalRepository(
            rescueEvent,
            coroutineScope
        ).first()

        if (isSuccess) {
            log.d(
                "CheckAllMyRescueEventsUtilImpl",
                "insertRescueEventInLocalRepo: Rescue event ${rescueEvent.id} added to local database"
            )
            insertCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = rescueEvent.id,
                    savedBy = myUid,
                    section = Section.RESCUE_EVENTS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowId ->

                if (rowId > 0) {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "insertRescueEventInLocalRepo: ${rescueEvent.id} added to local cache in section ${Section.RESCUE_EVENTS}"
                    )
                } else {
                    log.e(
                        "CheckAllMyRescueEventsUtilImpl",
                        "insertRescueEventInLocalRepo: Error adding ${rescueEvent.id} to local cache in section ${Section.RESCUE_EVENTS}"
                    )
                }
            }
        } else {
            log.e(
                "CheckAllMyRescueEventsUtilImpl",
                "insertRescueEventInLocalRepo: Error adding the rescue event ${rescueEvent.id} to local database"
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun modifyRescueEventInLocalRepo(
        updatedRescueEvent: RescueEvent,
        previousRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        val isSuccess = modifyRescueEventInLocalRepository(
            updatedRescueEvent = updatedRescueEvent,
            previousRescueEvent = previousRescueEvent,
            coroutineScope = coroutineScope
        ).first()

        if (isSuccess) {
            log.d(
                "CheckAllMyRescueEventsUtilImpl",
                "modifyRescueEventInLocalRepo: Rescue event ${updatedRescueEvent.id} modified in local database"
            )
            modifyCacheInLocalRepository(
                LocalCache(
                    cachedObjectId = updatedRescueEvent.id,
                    savedBy = myUid,
                    section = Section.RESCUE_EVENTS,
                    timestamp = Clock.System.now().epochSeconds
                )
            ) { rowsUpdated ->

                if (rowsUpdated > 0) {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "modifyRescueEventInLocalRepo: ${updatedRescueEvent.id} updated in local cache in section ${Section.RESCUE_EVENTS}"
                    )
                } else {
                    log.e(
                        "CheckAllMyRescueEventsUtilImpl",
                        "modifyRescueEventInLocalRepo: Error updating ${updatedRescueEvent.id} in local cache in section ${Section.RESCUE_EVENTS}"
                    )
                }
            }
        } else {
            log.e(
                "CheckAllMyRescueEventsUtilImpl",
                "modifyRescueEventInLocalRepo: Error modifying the rescue event ${updatedRescueEvent.id} in local database"
            )
        }
    }

    private fun deleteLocalRescueEvent(
        id: String,
        creatorId: String,
        coroutineScope: CoroutineScope
    ) {
        deleteRescueEventUtil.deleteRescueEvent(
            id = id,
            creatorId = creatorId,
            coroutineScope = coroutineScope,
            deleteOnLocal = true,
            deleteOnRemote = false,
            onError = {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "deleteLocalRescueEvent: Error deleting the local rescue event $id after the chat has been finished"
                )
            },
            onComplete = {
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "deleteLocalRescueEvent: Local rescue event $id deleted after the chat has been finished"
                )
            }
        )
    }
}
