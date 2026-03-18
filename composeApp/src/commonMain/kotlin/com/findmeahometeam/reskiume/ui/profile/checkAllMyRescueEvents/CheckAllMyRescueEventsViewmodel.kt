package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyRescueEvents
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil
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

                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllMyRescueEventsFromRemoteRepository(myUid)

                        checkAllMyRescueEventsUtil.downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onCompletionUpdateCache = {
                        val allRescueEventsFlow: Flow<List<RescueEvent>> =
                            getAllMyRescueEventsFromRemoteRepository(myUid)

                        checkAllMyRescueEventsUtil.downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow(
                            allRescueEventsFlow,
                            myUid,
                            viewModelScope
                        )
                    },
                    onVerifyCacheIsRecent = {
                        getAllMyRescueEventsFromLocalRepository(myUid)
                    }
                ).map { list ->
                    list.map { rescueEvent ->
                        UiRescueEvent(
                            rescueEvent = rescueEvent.copy(
                                imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                                    rescueEvent.imageUrl
                                } else {
                                    getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                                }
                            ),
                            uiAllNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    nonHumanAnimalToRescue.nonHumanAnimalId,
                                    nonHumanAnimalToRescue.caregiverId,
                                    viewModelScope
                                ).firstOrNull()
                            }
                        )
                    }.sortedBy { uiRescueEvent -> uiRescueEvent.rescueEvent.city }
                }
            }.toUiState()
}

data class UiRescueEvent(
    val rescueEvent: RescueEvent,
    val uiAllNonHumanAnimalsToRescue: List<NonHumanAnimal>,
    val distance: Double? = null,
    val owner: User? = null
)
