package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyRescueEvents
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyRescueEventsViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertRescueEventWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyRescueEventWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val konnectivity: Konnectivity = mock {
        every { isConnected } returns true
        every { currentNetworkConnection } returns NetworkConnection.WIFI
        every { isConnectedState } returns MutableStateFlow(true)
        every { currentNetworkConnectionState } returns MutableStateFlow(NetworkConnection.WIFI)
    }

    private fun getCheckAllMyRescueEventsViewmodel(
        getLocalCacheEntityReturnForMyRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.creatorId,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        getLocalCacheEntityReturnForRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        myRemoteRescueEvents: Flow<List<RemoteRescueEvent>> = flowOf(listOf(rescueEvent.toData())),
        insertedRescueEventInLocalRowIdArg: Long = 1L,
        insertedRescueEventWithoutImageInLocalRowIdArg: Long = 1L,
        insertedNeedToCoverForRescueEventInLocalRowIdArg: Long = 1L,
        insertedSecondNeedToCoverForRescueEventInLocalRowIdArg: Long = 1L,
        insertedNonHumanAnimalToRescueForRescueEventInLocalRowIdArg: Long = 1L,
        insertedSecondNonHumanAnimalToRescueForRescueEventInLocalRowIdArg: Long = 1L,
        modifiedRescueEventInLocalRowsUpdatedArg: Int = 1,
        modifiedRescueEventWithoutImageInLocalRowsUpdatedArg: Int = 1,
        myRescueEventWithAllNonHumanAnimalLocalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
        allMyRescueEventWithAllNonHumanAnimalLocalDataReturn: Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> = flowOf(
            listOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
        ),
        allMyManagedRescueEventsFromLocalReturn: Flow<List<RescueEvent>> = flowOf(listOf(rescueEvent))
    ): CheckAllMyRescueEventsViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckAllMyRescueEvents>(any(), any())
            } returns CheckAllMyRescueEvents(user.uid)
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForMyRescueEvent

            everySuspend {
                getLocalCacheEntity(
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForRescueEvent

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(localCacheIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            everySuspend {
                getAllMyRemoteRescueEvents(
                    rescueEvent.creatorId
                )
            } returns myRemoteRescueEvents
        }

        val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf((nonHumanAnimal))

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf((nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")))
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onInsertRescueEvent)
                )
            } calls {
                onInsertRescueEvent.get().invoke(insertedRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onInsertRescueEventWithoutImage)
                )
            } calls {
                onInsertRescueEventWithoutImage.get()
                    .invoke(insertedRescueEventWithoutImageInLocalRowIdArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[0].toEntity(),
                    capture(onInsertNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertNeedToCoverForRescueEvent.get()
                    .invoke(insertedNeedToCoverForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[1].toEntity(),
                    capture(onInsertSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertSecondNeedToCoverForRescueEvent.get()
                    .invoke(insertedSecondNeedToCoverForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    capture(onInsertNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedNonHumanAnimalToRescueForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    capture(onInsertSecondNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedSecondNonHumanAnimalToRescueForRescueEventInLocalRowIdArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onModifyRescueEvent)
                )
            } calls {
                onModifyRescueEvent.get().invoke(modifiedRescueEventInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onModifyRescueEventWithoutImage)
                )
            } calls {
                onModifyRescueEventWithoutImage.get()
                    .invoke(modifiedRescueEventWithoutImageInLocalRowsUpdatedArg)
            }

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns myRescueEventWithAllNonHumanAnimalLocalDataReturn

            every {
                getAllMyRescueEvents(rescueEvent.creatorId)
            } returns allMyRescueEventWithAllNonHumanAnimalLocalDataReturn
        }

        val checkAllMyRescueEventsUtil: CheckAllMyRescueEventsUtil = mock {

            every {
                downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                    any(),
                    user.uid,
                    any()
                )
            } returns allMyManagedRescueEventsFromLocalReturn
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl
        }

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllMyRescueEventsFromRemoteRepository =
            GetAllMyRescueEventsFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllMyRescueEventsFromLocalRepository =
            GetAllMyRescueEventsFromLocalRepository(localRescueEventRepository)

        return CheckAllMyRescueEventsViewmodel(
            saveStateHandleProvider,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllMyRescueEventsFromRemoteRepository,
            checkAllMyRescueEventsUtil,
            getAllMyRescueEventsFromLocalRepository,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil
        )
    }

    @Test
    fun `given a user requesting their rescue events_when the user enters on their rescue events section_then rescue events are saved in the local repository and displayed`() =
        runTest {
            val checkAllMyRescueEventsViewmodel = getCheckAllMyRescueEventsViewmodel(
                getLocalCacheEntityReturnForMyRescueEvent = null,
                getLocalCacheEntityReturnForRescueEvent = null,
            )

            checkAllMyRescueEventsViewmodel.fetchAllMyRescueEvents().test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ), awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "userUid123 added to local cache in section RESCUE_EVENTS"
                )
            }
        }

    @Test
    fun `given a user requesting their rescue events with outdated cache_when the user enters on their rescue events section_then REs are modified in the local repo and displayed`() =
        runTest {
            val checkAllMyRescueEventsViewmodel = getCheckAllMyRescueEventsViewmodel(
                getLocalCacheEntityReturnForMyRescueEvent =
                    localCache.copy(
                        cachedObjectId = rescueEvent.creatorId,
                        section = Section.RESCUE_EVENTS,
                        timestamp = 123L
                    ).toEntity(),
                getLocalCacheEntityReturnForRescueEvent =
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS,
                        timestamp = 123L
                    ).toEntity()
            )

            checkAllMyRescueEventsViewmodel.fetchAllMyRescueEvents().test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "userUid123 updated in local cache in section RESCUE_EVENTS"
                )
            }
        }

    @Test
    fun `given a user requesting their rescue events with recent cache_when the user enters on their rescue events section_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllMyRescueEventsViewmodel = getCheckAllMyRescueEventsViewmodel()

            checkAllMyRescueEventsViewmodel.fetchAllMyRescueEvents().test {
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for userUid123 in section RESCUE_EVENTS is up-to-date."
                )
            }
        }
}
