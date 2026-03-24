package com.findmeahometeam.reskiume.ui.unitTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent.CreateRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateRescueEventsViewmodelTest : CoroutineTestDispatcher() {

    private val onRequestEnableLocation = Capture.slot<(isEnabled: Boolean) -> Unit>()

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onUploadImageToRemoteForRescueEvent = Capture.slot<(imagePath: String) -> Unit>()

    private val onInsertRemoteRescueEvent = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifySecondRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertRescueEventWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val modifySecondNonHumanAnimalInLocalRepository =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    @OptIn(ExperimentalTime::class)
    private val createdRescueEventId = Clock.System.now().epochSeconds.toString() + user.uid

    private fun getCreateRescueEventViewmodel(
        locationReturn: Pair<Double, Double> = Pair(rescueEvent.longitude, rescueEvent.latitude),
        authStateReturn: AuthUser? = authUser,
        localCacheCreatedInLocalDatasourceArg: Long = 1L,
        databaseResultOfCreatingRescueEventsInRemoteRepoArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingSecondNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        imagePathToUploadToRemoteForRescueEvent: String = rescueEvent.imageUrl,
        insertedRowIdOfNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfRescueEventWithoutImageInLocalArg: Long = 1L,
        numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1,
        numberOfSecondNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1
    ): CreateRescueEventViewmodel {


        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "second",
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(modifySecondNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifySecondNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfSecondNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }
        }

        val locationRepository: LocationRepository = mock {

            everySuspend {
                observeIfLocationEnabledFlow()
            } returns flowOf(true)

            every {
                requestEnableLocation(
                    capture(onRequestEnableLocation)
                )
            } calls {
                onRequestEnableLocation.get().invoke(true)
            }

            everySuspend {
                getLocation()
            } returns locationReturn
        }

        val storageRepository: StorageRepository = mock {

            every {
                uploadImage(
                    user.uid,
                    any(),
                    Section.RESCUE_EVENTS,
                    rescueEvent.imageUrl,
                    capture(onUploadImageToRemoteForRescueEvent)
                )
            } calls {
                onUploadImageToRemoteForRescueEvent.get()
                    .invoke(imagePathToUploadToRemoteForRescueEvent)
            }
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = mock {

            everySuspend {
                deleteNonHumanAnimal(
                    id = nonHumanAnimal.id,
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
            }

            everySuspend {
                deleteNonHumanAnimal(
                    id = nonHumanAnimal.id + "second",
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
            }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            everySuspend {

                insertRemoteRescueEvent(
                    rescueEvent.copy(
                        id = createdRescueEventId,
                        allNeedsToCover = rescueEvent.allNeedsToCover.map {
                            it.copy(rescueEventId = createdRescueEventId)
                        },
                        allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                            it.copy(rescueEventId = createdRescueEventId)
                        }
                    ).toData(),
                    capture(onInsertRemoteRescueEvent)
                )
            } calls {
                onInsertRemoteRescueEvent.get()
                    .invoke(databaseResultOfCreatingRescueEventsInRemoteRepoArg)
            }

            everySuspend {

                insertRemoteRescueEvent(
                    rescueEvent.copy(
                        id = createdRescueEventId,
                        imageUrl = "",
                        allNeedsToCover = rescueEvent.allNeedsToCover.map {
                            it.copy(rescueEventId = createdRescueEventId)
                        },
                        allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.map {
                            it.copy(rescueEventId = createdRescueEventId)
                        }
                    ).toData(),
                    capture(onInsertRemoteRescueEvent)
                )
            } calls {
                onInsertRemoteRescueEvent.get()
                    .invoke(databaseResultOfCreatingRescueEventsInRemoteRepoArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    getRemoteNonHumanAnimal(
                        nonHumanAnimal.id + "second",
                        nonHumanAnimal.caregiverId
                    )
                } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(
                            adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                        ).toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(
                            id = nonHumanAnimal.id + "second",
                            adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                        ).toData(),
                        capture(onModifySecondRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifySecondRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingSecondNonHumanAnimalInRemoteRepositoryArg)
                }
            }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].copy(
                        rescueEventId = createdRescueEventId
                    ).toEntity(),
                    capture(onInsertNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].copy(
                        rescueEventId = createdRescueEventId
                    ).toEntity(),
                    capture(onInsertSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertSecondNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                        rescueEventId = createdRescueEventId
                    ).toEntity(),
                    capture(onInsertNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].copy(
                        rescueEventId = createdRescueEventId
                    ).toEntity(),
                    capture(onInsertSecondNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertSecondNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(
                        id = createdRescueEventId,
                        savedBy = authUser.uid
                    ).toEntity(),
                    capture(onInsertRescueEvent)
                )
            } calls {
                onInsertRescueEvent.get().invoke(insertedRowIdOfRescueEventInLocalArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(
                        id = createdRescueEventId,
                        savedBy = authUser.uid,
                        imageUrl = ""
                    ).toEntity(),
                    capture(onInsertRescueEventWithoutImage)
                )
            } calls {
                onInsertRescueEventWithoutImage.get()
                    .invoke(insertedRowIdOfRescueEventWithoutImageInLocalArg)
            }
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls { onInsertLocalCacheEntity.get().invoke(localCacheCreatedInLocalDatasourceArg) }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal)

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        }

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

        val getStringProvider: StringProvider = mock {
            everySuspend {
                getStringResource(any())
            } returns "I found a non-human animal in the street. What can I do?"
        }

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
            getStringProvider,
            uploadImageToRemoteDataSource,
            insertRescueEventInRemoteRepository,
            insertRescueEventInLocalRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given my rescue event to create_when I want to add non human animals to rescue_then the rescue event list available non human animals`() =
        runTest {
            getCreateRescueEventViewmodel().allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my rescue event to create_when I add non human animals to rescue and needs to cover with my rescue event location_then I click to create my rescue event`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel()

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to create_when I add non human animals to rescue and needs to cover without my rescue event location_then an error is displayed`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel()

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
            val createRescueEventViewmodel = getCreateRescueEventViewmodel()

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent.copy(imageUrl = ""))

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }

            verify {
                log.d(
                    "CreateRescueEventViewmodel",
                    "uploadNewImageToRemoteDataSource: the download URI from the rescue event $createdRescueEventId is blank"
                )
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails creating the rescue event in the remote repo_then the app retrieves an error`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                databaseResultOfCreatingRescueEventsInRemoteRepoArg = DatabaseResult.Error()
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateRescueEventViewmodel",
                    "createRescueEventInRemoteDataSource: failed to create the rescue event $createdRescueEventId in the remote data source"
                )
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails creating the rescue event in the local repo_then the app retrieves an error`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                insertedRowIdOfRescueEventInLocalArg = 0
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateRescueEventViewmodel",
                    "createRescueEventInLocalDataSource: failed to create the rescue event $createdRescueEventId in the local data source"
                )
            }
        }

    @Test
    fun `given my rescue event to create_when I add my rescue event data but fails inserting the rescue event cache_then the rescue event is created`() =
        runTest {
            val createRescueEventViewmodel = getCreateRescueEventViewmodel(
                localCacheCreatedInLocalDatasourceArg = 0
            )

            createRescueEventViewmodel.updateLocation()

            createRescueEventViewmodel.createRescueEvent(rescueEvent)

            createRescueEventViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateRescueEventViewmodel",
                    "createCacheForRescueEventInLocalDataSource: Error creating $createdRescueEventId in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
}
