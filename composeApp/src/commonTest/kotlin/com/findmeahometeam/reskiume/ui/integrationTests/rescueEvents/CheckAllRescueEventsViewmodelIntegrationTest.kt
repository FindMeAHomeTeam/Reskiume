package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.activistLatitude
import com.findmeahometeam.reskiume.activistLongitude
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.rescueEvents.checkAllRescueEvents.CheckAllRescueEventsViewmodel
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocationRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStringProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckAllRescueEventsViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllRescueEventsViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        ),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        locationRepository: LocationRepository = FakeLocationRepository(
            location = Pair(
                activistLongitude,
                activistLatitude
            )
        ),
        storageRepository: StorageRepository = FakeStorageRepository(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity(),
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
            )
        ),
        stringProvider: StringProvider = FakeStringProvider("I found a non-human animal in the street. What can I do?"),
        log: Log = FakeLog()
    ): CheckAllRescueEventsViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllRescueEventsByCountryAndCityFromRemoteRepository =
            GetAllRescueEventsByCountryAndCityFromRemoteRepository(
                fireStoreRemoteRescueEventRepository
            )

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        val insertRescueEventInLocalRepository =
            InsertRescueEventInLocalRepository(
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                manageImagePath,
                authRepository,
                log
            )

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val modifyRescueEventInLocalRepository =
            ModifyRescueEventInLocalRepository(
                manageImagePath,
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository = ModifyCacheInLocalRepository(localCacheRepository)

        val checkAllMyRescueEventsUtil = CheckAllMyRescueEventsUtilImpl(
            downloadImageToLocalDataSource,
            getRescueEventFromLocalRepository,
            insertRescueEventInLocalRepository,
            insertCacheInLocalRepository,
            modifyRescueEventInLocalRepository,
            modifyCacheInLocalRepository,
            log
        )

        val getAllRescueEventsByCountryAndCityFromLocalRepository =
            GetAllRescueEventsByCountryAndCityFromLocalRepository(localRescueEventRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllRescueEventsByLocationFromRemoteRepository =
            GetAllRescueEventsByLocationFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val getAllRescueEventsByLocationFromLocalRepository =
            GetAllRescueEventsByLocationFromLocalRepository(localRescueEventRepository)

        val observeIfLocationEnabledFromLocationRepository =
            ObserveIfLocationEnabledFromLocationRepository(locationRepository)

        val requestEnableLocationFromLocationRepository =
            RequestEnableLocationFromLocationRepository(locationRepository)

        val getLocationFromLocationRepository =
            GetLocationFromLocationRepository(locationRepository)

        return CheckAllRescueEventsViewmodel(
            observeAuthStateInAuthDataSource,
            stringProvider,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllRescueEventsByCountryAndCityFromRemoteRepository,
            checkAllMyRescueEventsUtil,
            getAllRescueEventsByCountryAndCityFromLocalRepository,
            checkNonHumanAnimalUtil,
            getImagePathForFileNameFromLocalDataSource,
            getAllRescueEventsByLocationFromRemoteRepository,
            getAllRescueEventsByLocationFromLocalRepository,
            observeIfLocationEnabledFromLocationRepository,
            requestEnableLocationFromLocationRepository,
            getLocationFromLocationRepository,
            log
        )
    }

    @Test
    fun `given a user requesting all rescue events from cordoba_when the user clicks on the search button_then rescue events are saved in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs from cordoba_when the user clicks on the search button but some REs do not have avatar_then REs are saved without image in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.copy(imageUrl = "").toData())
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = ""),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs from cordoba_when the user clicks on the search button but the app fails saving REs in the local cache_then REs are not saved in the local cache but displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all rescue events from cordoba with outdated cache_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
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
                            cachedObjectId = rescueEvent.country + rescueEvent.city,
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
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
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
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all rescue events from cordoba with outdated cache and no image_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.copy(imageUrl = "").toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            rescueEventEntity = rescueEvent.copy(
                                imageUrl = ""
                            ).toEntity()
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.country + rescueEvent.city,
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
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = ""),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                )
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs from cordoba with recent cache_when the user clicks on the search button_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.country + rescueEvent.city,
                            section = Section.RESCUE_EVENTS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByPlace(
                rescueEvent.country,
                rescueEvent.city
            )

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Loading }
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
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs by location_when the user clicks on the search button_then REs are saved in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ),
                                22.1
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs by location_when the user clicks on the search button but there is an error updating the location_then an error is displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                locationRepository = FakeLocationRepository(
                    location = Pair(0.0, 0.0)
                ),
                stringProvider = FakeStringProvider("Please, turn on the location to get your position")
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all rescue events by location with outdated cache_when the user clicks on the search button_then rescue events are modified in the local repository and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
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
                            cachedObjectId = "${activistLongitude}${activistLatitude}",
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
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ),
                                22.1
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a user requesting all REs by location with recent cache_when the user clicks on the search button_then REs are retrieved from local cache and displayed`() =
        runTest {
            val checkAllRescueEventsViewmodel = getCheckAllRescueEventsViewmodel(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = "${activistLongitude}${activistLatitude}",
                            section = Section.RESCUE_EVENTS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            )
            checkAllRescueEventsViewmodel.fetchAllRescueEventsStateByLocation()

            checkAllRescueEventsViewmodel.allRescueEventsState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        listOf(
                            UiRescueEvent(
                                rescueEvent,
                                listOf(
                                    nonHumanAnimal,
                                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                                ),
                                22.1
                            )
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }
}
