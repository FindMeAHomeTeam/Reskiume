package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent.CreateRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocationRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStringProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateRescueEventViewmodelIntegrationTest : CoroutineTestDispatcher() {

    @OptIn(ExperimentalTime::class)
    private val createdRescueEventId = Clock.System.now().epochSeconds.toString() + user.uid

    private fun getCreateRescueEventViewmodel(
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        locationRepository: LocationRepository = FakeLocationRepository(
            Pair(
                rescueEvent.longitude,
                rescueEvent.latitude
            )
        ),
        storageRepository: StorageRepository = FakeStorageRepository(),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = FakeDeleteNonHumanAnimalUtil(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(nonHumanAnimal, nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        ),
        stringProvider: StringProvider = FakeStringProvider("Text to display"),
        log: Log = FakeLog()
    ): CreateRescueEventViewmodel {

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val observeIfLocationEnabledFromLocationRepository =
            ObserveIfLocationEnabledFromLocationRepository(locationRepository)

        val requestEnableLocationFromLocationRepository =
            RequestEnableLocationFromLocationRepository(locationRepository)

        val getLocationFromLocationRepository =
            GetLocationFromLocationRepository(locationRepository)

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertRescueEventInRemoteRepository =
            InsertRescueEventInRemoteRepository(
                authRepository,
                fireStoreRemoteRescueEventRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

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

        return CreateRescueEventViewmodel(
            getAllNonHumanAnimalsFromLocalRepository,
            observeIfLocationEnabledFromLocationRepository,
            requestEnableLocationFromLocationRepository,
            getLocationFromLocationRepository,
            observeAuthStateInAuthDataSource,
            stringProvider,
            uploadImageToRemoteDataSource,
            insertRescueEventInRemoteRepository,
            insertRescueEventInLocalRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given my rescue event to create_when I want to add non human animals to rescue_then rescue event list available non human animals`() =
        runTest {
            getCreateRescueEventViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity()
                    )
                )
            ).allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my rescue event to create_when I add needs to cover and non human animals to rescue with my rescue event location_then I click to create my rescue event`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData()
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                )
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add needs to cover and non human animals to rescue without my rescue event location_then an error is displayed`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                stringProvider = FakeStringProvider("Please, turn on the location to get your position"),
            )

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but there is no rescue event image_then the rescue event is created`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData()
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                )
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent.copy(imageUrl = ""))

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails creating the rescue event in the remote repo_then the app retrieves an error`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData()
                    )
                ),
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(
                        rescueEvent.copy(id = createdRescueEventId).toData()
                    )
                )
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails creating the rescue event in the local repo_then the app retrieves an error`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData()
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                )
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails inserting the rescue event cache_then the rescue event is created`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData()
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = createdRescueEventId,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
