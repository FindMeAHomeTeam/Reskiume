package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyRescueEvents
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckAllMyRescueEventsUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyRescueEventsViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllMyRescueEventsViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            CheckAllMyRescueEvents(user.uid)
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(nonHumanAnimal, nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        checkAllMyRescueEventsUtil: CheckAllMyRescueEventsUtil = FakeCheckAllMyRescueEventsUtil()
    ): CheckAllMyRescueEventsViewmodel {

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
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                )
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
        }

    @Test
    fun `given a user requesting their rescue events with outdated cache_when the user enters on their rescue events section_then REs are modified in the local repo and displayed`() =
        runTest {
            val checkAllMyRescueEventsViewmodel = getCheckAllMyRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.creatorId,
                            section = Section.RESCUE_EVENTS,
                            timestamp = 123L
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
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
        }

    @Test
    fun `given a user requesting their rescue events with recent cache_when the user enters on their rescue events section_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllMyRescueEventsViewmodel = getCheckAllMyRescueEventsViewmodel(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.creatorId,
                            section = Section.RESCUE_EVENTS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
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
        }
}
