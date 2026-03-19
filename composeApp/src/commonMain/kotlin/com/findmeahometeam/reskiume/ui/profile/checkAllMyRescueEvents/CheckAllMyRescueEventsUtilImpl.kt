package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckAllMyRescueEventsUtilImpl(
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val getRescueEventFromLocalRepository: GetRescueEventFromLocalRepository,
    private val insertRescueEventInLocalRepository: InsertRescueEventInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val modifyRescueEventInLocalRepository: ModifyRescueEventInLocalRepository,
    private val modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    private val log: Log
) : CheckAllMyRescueEventsUtil {

    override fun downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
        allRescueEventsFlow: Flow<List<RescueEvent>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<RescueEvent>> =
        allRescueEventsFlow.map { rescueEventList ->
            rescueEventList.map { rescueEvent ->

                val localRescueEvent: RescueEvent? = getRescueEventFromLocalRepository(
                    rescueEvent.id
                ).firstOrNull()

                if (rescueEvent.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEvent.creatorId,
                        extraId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val rescueEventWithLocalImage =
                        rescueEvent.copy(imageUrl = localImagePath.ifBlank { rescueEvent.imageUrl })

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
                    rescueEventWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "downloadImageAndManageRescueEventsInLocalRepositoryFromFlow: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                    )

                    if (localRescueEvent == null) {
                        insertRescueEventInLocalRepo(
                            rescueEvent = rescueEvent,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    } else {
                        modifyRescueEventInLocalRepo(
                            updatedRescueEvent = rescueEvent,
                            previousRescueEvent = localRescueEvent,
                            coroutineScope = coroutineScope,
                            myUid = myUid
                        )
                    }
                    rescueEvent
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    private suspend fun insertRescueEventInLocalRepo(
        rescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        insertRescueEventInLocalRepository(
            rescueEvent,
            coroutineScope
        ) { isSuccess ->
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

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun modifyRescueEventInLocalRepo(
        updatedRescueEvent: RescueEvent,
        previousRescueEvent: RescueEvent,
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        modifyRescueEventInLocalRepository(
            updatedRescueEvent,
            previousRescueEvent,
            coroutineScope
        ) { isSuccess ->
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
    }

    override fun downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow(
        allRescueEventsFlow: Flow<List<RescueEvent>>,
        myUid: String,
        coroutineScope: CoroutineScope
    ): Flow<List<RescueEvent>> =
        allRescueEventsFlow.map { rescueEventList ->
            rescueEventList.map { updatedRescueEvent ->

                val previousRescueEvent: RescueEvent = getRescueEventFromLocalRepository(
                    updatedRescueEvent.id
                ).first()!!

                if (updatedRescueEvent.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = updatedRescueEvent.creatorId,
                        extraId = updatedRescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val updatedRescueEventWithLocalImage =
                        updatedRescueEvent.copy(imageUrl = localImagePath.ifBlank { updatedRescueEvent.imageUrl })

                    modifyRescueEventInLocalRepo(
                        updatedRescueEvent = updatedRescueEventWithLocalImage,
                        previousRescueEvent = previousRescueEvent,
                        coroutineScope = coroutineScope,
                        myUid = myUid
                    )
                    updatedRescueEventWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow: Rescue event ${updatedRescueEvent.id} has no avatar image to save locally."
                    )
                    modifyRescueEventInLocalRepo(
                        updatedRescueEvent = updatedRescueEvent,
                        previousRescueEvent = previousRescueEvent,
                        coroutineScope = coroutineScope,
                        myUid = myUid
                    )
                    updatedRescueEvent
                }
            }
        }
}
