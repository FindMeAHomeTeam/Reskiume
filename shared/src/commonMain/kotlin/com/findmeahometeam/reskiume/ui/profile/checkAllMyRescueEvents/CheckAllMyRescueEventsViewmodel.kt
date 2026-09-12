package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyRescueEvents
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CheckAllMyRescueEventsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllMyRescueEventsFromRemoteRepository: GetAllMyRescueEventsFromRemoteRepository,
    private val checkAllMyRescueEventsUtil: CheckAllMyRescueEventsUtil,
    private val getAllMyRescueEventsFromLocalRepository: GetAllMyRescueEventsFromLocalRepository,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val deleteRescueEventUtil: DeleteRescueEventUtil,
    private val getChatFromRemoteRepository: GetChatFromRemoteRepository,
    private val log: Log
) : ViewModel() {

    private val myUid =
        saveStateHandleProvider.provideObjectRoute(CheckAllMyRescueEvents::class).myUid

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchAllMyRescueEvents(): Flow<UiState<List<UiRescueEvent>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = myUid,
                    savedBy = myUid,
                    section = Section.RESCUE_EVENTS,
                    onCompletionInsertCache = {

                        manageAllMyRescueEvents()
                        getAllMyRescueEventsFromLocalRepository(myUid)
                    },
                    onCompletionUpdateCache = {
                        manageAllMyRescueEvents()
                        getAllMyRescueEventsFromLocalRepository(myUid)
                    },
                    onVerifyCacheIsRecent = {
                        getAllMyRescueEventsFromLocalRepository(myUid)
                    }
                ).map { list ->
                    list.mapNotNull { rescueEvent ->

                        when {
                            rescueEvent.allNonHumanAnimalsToRescue.isEmpty() -> {

                                deleteRescueEvent(
                                    id = rescueEvent.id,
                                    creatorId = rescueEvent.creatorId,
                                    deleteOnRemote = false
                                )
                                null
                            }
                            getChatFromRemoteRepository(
                                id = rescueEvent.id + rescueEvent.creatorId,
                                myUid = myUid
                            ).first() == null -> {

                                deleteRescueEvent(
                                    id = rescueEvent.id,
                                    creatorId = rescueEvent.creatorId,
                                    deleteOnRemote = true
                                )
                                null
                            }
                            else -> {
                                UiRescueEvent(
                                    rescueEvent = rescueEvent.copy(
                                        imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                                            rescueEvent.imageUrl
                                        } else {
                                            getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                                        }
                                    ),
                                    allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                            nonHumanAnimalToRescue.nonHumanAnimalId,
                                            nonHumanAnimalToRescue.caregiverId,
                                            viewModelScope
                                        ).firstOrNull()
                                    }
                                )
                            }
                        }
                    }.sortedBy { uiRescueEvent -> uiRescueEvent.rescueEvent.city }
                }
            }.toUiState()

    private suspend fun manageAllMyRescueEvents() {
        val allRemoteRescueEvents: List<RescueEvent> =
            getAllMyRescueEventsFromRemoteRepository(myUid).first()

        val allLocalRescueEvents: List<RescueEvent> =
            getAllMyRescueEventsFromLocalRepository(myUid).first()

        checkAllMyRescueEventsUtil.updateLocalRepositoryWithRemoteRescueEvents(
            allRemoteRescueEvents.toSet(),
            allLocalRescueEvents.toSet(),
            myUid,
            viewModelScope
        )
    }

    private fun deleteRescueEvent(
        id: String,
        creatorId: String,
        deleteOnLocal: Boolean = true,
        deleteOnRemote: Boolean
    ) {
        val environment = if(deleteOnLocal && deleteOnRemote) "both" else if (deleteOnRemote) "remote" else "local"

        deleteRescueEventUtil.deleteRescueEvent(
            id = id,
            creatorId = creatorId,
            coroutineScope = viewModelScope,
            deleteOnLocal = deleteOnLocal,
            deleteOnRemote = deleteOnRemote,
            onError = {
                log.e(
                    "CheckAllMyRescueEventsViewmodel",
                    "deleteLocalRescueEvent: Error deleting the $environment rescue event $id after the chat has been finished"
                )
            },
            onComplete = {
                log.d(
                    "CheckAllMyRescueEventsViewmodel",
                    "deleteLocalRescueEvent: $environment rescue event $id deleted after the chat has been finished"
                )
            }
        )
    }
}

data class UiRescueEvent(
    val rescueEvent: RescueEvent,
    val allUiNonHumanAnimalsToRescue: List<NonHumanAnimal>,
    val distance: Double? = null,
    val creator: User? = null,
    val isContentUpdated: Boolean = false
)
