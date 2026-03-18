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

                if (rescueEvent.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEvent.creatorId,
                        extraId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val rescueEventWithLocalImage =
                        rescueEvent.copy(imageUrl = localImagePath.ifBlank { rescueEvent.imageUrl })

                    val localRescueEvent: RescueEvent? = getRescueEventFromLocalRepository(
                        rescueEvent.id
                    ).firstOrNull()

                    if (localRescueEvent == null) {
                        insertRescueEventInLocalRepo(
                            rescueEventWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyRescueEventInLocalRepo(
                            rescueEventWithLocalImage,
                            coroutineScope,
                            myUid
                        )
                    }
                    rescueEventWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "downloadImageAndManageRescueEventsInLocalRepositoryFromFlow: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                    )
                    val localRescueEvent: RescueEvent? = getRescueEventFromLocalRepository(
                        rescueEvent.id
                    ).firstOrNull()

                    if (localRescueEvent == null) {
                        insertRescueEventInLocalRepo(
                            rescueEvent,
                            coroutineScope,
                            myUid
                        )
                    } else {
                        modifyRescueEventInLocalRepo(
                            rescueEvent,
                            coroutineScope,
                            myUid
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
        coroutineScope: CoroutineScope,
        myUid: String
    ) {
        val previousRescueEvent = getRescueEventFromLocalRepository(updatedRescueEvent.id).first()!!

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
            rescueEventList.map { rescueEvent ->

                if (rescueEvent.imageUrl.isNotBlank()) {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = rescueEvent.creatorId,
                        extraId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    )
                    val rescueEventWithLocalImage =
                        rescueEvent.copy(imageUrl = localImagePath.ifBlank { rescueEvent.imageUrl })

                    modifyRescueEventInLocalRepo(
                        rescueEventWithLocalImage,
                        coroutineScope,
                        myUid
                    )
                    rescueEventWithLocalImage
                } else {
                    log.d(
                        "CheckAllMyRescueEventsUtilImpl",
                        "downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                    )
                    modifyRescueEventInLocalRepo(
                        rescueEvent,
                        coroutineScope,
                        myUid
                    )
                    rescueEvent
                }
            }
        }
}
