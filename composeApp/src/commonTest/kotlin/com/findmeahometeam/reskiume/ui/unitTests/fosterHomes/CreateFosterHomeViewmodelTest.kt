package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.location.LocationRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome.CreateFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
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

class CreateFosterHomeViewmodelTest : CoroutineTestDispatcher() {

    private val onRequestEnableLocation = Capture.slot<(isEnabled: Boolean) -> Unit>()

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onUploadImageToRemoteForFosterHome = Capture.slot<(imagePath: String) -> Unit>()

    private val onInsertRemoteFosterHome = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertFosterHome = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertFosterHomeWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    @OptIn(ExperimentalTime::class)
    private val createdFosterHomeId = Clock.System.now().epochSeconds.toString() + user.uid

    private fun getCreateFosterHomeViewmodel(
        locationReturn: Pair<Double, Double> = Pair(fosterHome.longitude, fosterHome.latitude),
        authStateReturn: AuthUser? = authUser,
        localCacheCreatedInLocalDatasourceArg: Long = 1L,
        databaseResultOfCreatingFosterHomesInRemoteRepoArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        imagePathToUploadToRemoteForFosterHome: String = fosterHome.imageUrl,
        insertedAcceptedNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg: Long = 1L,
        insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg: Long = 1L,
        createdFosterHomeIdInLocalRepoArg: Long = 1L,
        createdFosterHomeIdWithoutImageInLocalRepoArg: Long = 1L,
        numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1
    ): CreateFosterHomeViewmodel {


        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.REHOMED,
                        fosterHomeId = createdFosterHomeId
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
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
                    Section.FOSTER_HOMES,
                    fosterHome.imageUrl,
                    capture(onUploadImageToRemoteForFosterHome)
                )
            } calls {
                onUploadImageToRemoteForFosterHome.get()
                    .invoke(imagePathToUploadToRemoteForFosterHome)
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
            } returns Unit
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {

                insertRemoteFosterHome(
                    fosterHome.copy(
                        id = createdFosterHomeId,
                        allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals.map {
                            it.copy(fosterHomeId = createdFosterHomeId)
                        },
                        allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                            it.copy(fosterHomeId = createdFosterHomeId)
                        }
                    ).toData(),
                    capture(onInsertRemoteFosterHome)
                )
            } calls {
                onInsertRemoteFosterHome.get()
                    .invoke(databaseResultOfCreatingFosterHomesInRemoteRepoArg)
            }

            everySuspend {

                insertRemoteFosterHome(
                    fosterHome.copy(
                        id = createdFosterHomeId,
                        imageUrl = "",
                        allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals.map {
                            it.copy(fosterHomeId = createdFosterHomeId)
                        },
                        allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.map {
                            it.copy(fosterHomeId = createdFosterHomeId)
                        }
                    ).toData(),
                    capture(onInsertRemoteFosterHome)
                )
            } calls {
                onInsertRemoteFosterHome.get()
                    .invoke(databaseResultOfCreatingFosterHomesInRemoteRepoArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(
                            adoptionState = AdoptionState.REHOMED,
                            fosterHomeId = createdFosterHomeId
                        ).toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }
            }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].copy(
                        fosterHomeId = createdFosterHomeId
                    ).toEntity(),
                    capture(onInsertAcceptedNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedNonHumanAnimalForFosterHome.get()
                    .invoke(insertedAcceptedNonHumanAnimalForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].copy(
                        fosterHomeId = createdFosterHomeId
                    ).toEntity(),
                    capture(onInsertAcceptedSecondNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedSecondNonHumanAnimalForFosterHome.get()
                    .invoke(insertedAcceptedSecondNonHumanAnimalForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].copy(
                        fosterHomeId = createdFosterHomeId
                    ).toEntity(),
                    capture(onInsertResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onInsertResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(insertedResidentNonHumanAnimalIdForFosterHomeInLocalRowIdArg)
            }

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(
                        id = createdFosterHomeId,
                        savedBy = authUser.uid
                    ).toEntity(),
                    capture(onInsertFosterHome)
                )
            } calls {
                onInsertFosterHome.get().invoke(createdFosterHomeIdInLocalRepoArg)
            }

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(
                        id = createdFosterHomeId,
                        savedBy = authUser.uid,
                        imageUrl = ""
                    ).toEntity(),
                    capture(onInsertFosterHomeWithoutImage)
                )
            } calls {
                onInsertFosterHomeWithoutImage.get()
                    .invoke(createdFosterHomeIdWithoutImageInLocalRepoArg)
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

            every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl

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

        val insertFosterHomeInRemoteRepository =
            InsertFosterHomeInRemoteRepository(
                authRepository,
                deleteNonHumanAnimalUtil,
                fireStoreRemoteFosterHomeRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                log
            )

        val insertFosterHomeInLocalRepository =
            InsertFosterHomeInLocalRepository(
                localFosterHomeRepository,
                manageImagePath,
                localNonHumanAnimalRepository,
                checkNonHumanAnimalUtil,
                authRepository,
                log
            )

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        return CreateFosterHomeViewmodel(
            getAllNonHumanAnimalsFromLocalRepository,
            observeIfLocationEnabledFromLocationRepository,
            requestEnableLocationFromLocationRepository,
            getLocationFromLocationRepository,
            observeAuthStateInAuthDataSource,
            getStringProvider,
            uploadImageToRemoteDataSource,
            insertFosterHomeInRemoteRepository,
            insertFosterHomeInLocalRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given my foster home to create_when I want to add residents_then foster home list available non human animals`() =
        runTest {
            getCreateFosterHomeViewmodel().allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my foster home to create_when I add accepted and resident non human animals with my foster home location_then I click to create my foster home`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel()

            createFosterHomeViewmodel.updateLocation()

            createFosterHomeViewmodel.createFosterHome(fosterHome)

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to create_when I add accepted and resident non human animals without my foster home location_then I click to create my foster home`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel()

            createFosterHomeViewmodel.createFosterHome(fosterHome)

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to create_when I add my foster home data but there is no foster home image_then the foster home is updated`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel()

            createFosterHomeViewmodel.updateLocation()

            createFosterHomeViewmodel.createFosterHome(fosterHome.copy(imageUrl = ""))

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }

            verify {
                log.d(
                    "CreateFosterHomeViewmodel",
                    "uploadNewImageToRemoteDataSource: the download URI from the foster home $createdFosterHomeId is blank"
                )
            }
        }

    @Test
    fun `given my foster home to create_when I add my foster home data but fails creating the foster home in the remote repo_then the app retrieves an error`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel(
                databaseResultOfCreatingFosterHomesInRemoteRepoArg = DatabaseResult.Error()
            )

            createFosterHomeViewmodel.updateLocation()

            createFosterHomeViewmodel.createFosterHome(fosterHome)

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateFosterHomeViewmodel",
                    "createFosterHomeInRemoteDataSource: failed to create the foster home $createdFosterHomeId in the remote data source"
                )
            }
        }

    @Test
    fun `given my foster home to create_when I add my foster home data but fails creating the foster home in the local repo_then the app retrieves an error`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel(
                createdFosterHomeIdInLocalRepoArg = 0
            )

            createFosterHomeViewmodel.updateLocation()

            createFosterHomeViewmodel.createFosterHome(fosterHome)

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateFosterHomeViewmodel",
                    "createFosterHomeInLocalDataSource: failed to create the foster home $createdFosterHomeId in the local data source"
                )
            }
        }

    @Test
    fun `given my foster home to create_when I add my foster home data but fails inserting the foster home cache_then the foster home is updated`() =
        runTest {
            val createFosterHomeViewmodel = getCreateFosterHomeViewmodel(
                localCacheCreatedInLocalDatasourceArg = 0
            )

            createFosterHomeViewmodel.updateLocation()

            createFosterHomeViewmodel.createFosterHome(fosterHome)

            createFosterHomeViewmodel.saveChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "CreateFosterHomeViewmodel",
                    "createCacheForFosterHomeInLocalDataSource: Error creating $createdFosterHomeId in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }
}
