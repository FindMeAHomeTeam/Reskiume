package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.ModifyFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteFosterHomeUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyFosterHomeViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getModifyFosterHomeViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            ModifyFosterHome(fosterHome.id)
        ),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = FakeDeleteNonHumanAnimalUtil(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        deleteFosterHomeUtil: DeleteFosterHomeUtil = FakeDeleteFosterHomeUtil(),
        log: Log = FakeLog()
    ): ModifyFosterHomeViewmodel {

        val getFosterHomeFromLocalRepository =
            GetFosterHomeFromLocalRepository(localFosterHomeRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val getFosterHomeFromRemoteRepository =
            GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val modifyFosterHomeInRemoteRepository =
            ModifyFosterHomeInRemoteRepository(
                observeAuthStateInAuthDataSource,
                fireStoreRemoteFosterHomeRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

        val modifyFosterHomeInLocalRepository =
            ModifyFosterHomeInLocalRepository(
                manageImagePath,
                localFosterHomeRepository,
                localNonHumanAnimalRepository,
                checkNonHumanAnimalUtil,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        return ModifyFosterHomeViewmodel(
            saveStateHandleProvider,
            getFosterHomeFromLocalRepository,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            getAllNonHumanAnimalsFromLocalRepository,
            getFosterHomeFromRemoteRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            uploadImageToRemoteDataSource,
            modifyFosterHomeInRemoteRepository,
            modifyFosterHomeInLocalRepository,
            modifyCacheInLocalRepository,
            deleteFosterHomeUtil,
            log
        )
    }

    @Test
    fun `given my foster home_when I click to modify it_then foster home is retrieved`() =
        runTest {
            getModifyFosterHomeViewmodel(
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                )
            ).fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome,
                            listOf(nonHumanAnimal)
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given my foster home to modify_when I want to add residents_then foster home list available non human animals`() =
        runTest {
            getModifyFosterHomeViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity()
                    )
                )
            ).allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my foster home to modify_when I add accepted and resident non human animals_then I click to update my foster home`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toData()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "789")
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toEntity()
                    )
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails deleting the remote foster home image_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                )
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when click to update my foster home but fails retrieving the local foster home_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    )
                )
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but fails deleting the local foster home image_then the app retrieves an error`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    )
                )
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, fosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when I click to update my foster home but there is no foster home image_then the foster home is updated`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toData()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "789")
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toEntity()
                    )
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
            )

            val acceptedNonHumanAnimal = AcceptedNonHumanAnimalForFosterHome(
                acceptedNonHumanAnimalId = 123456,
                fosterHomeId = fosterHome.id,
                acceptedNonHumanAnimalType = NonHumanAnimalType.BIRD,
                acceptedNonHumanAnimalGender = Gender.MALE
            )

            val residentNonHumanAnimal = ResidentNonHumanAnimalForFosterHome(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                fosterHomeId = fosterHome.id
            )

            val updatedFosterHome = fosterHome.copy(
                imageUrl = "",
                allAcceptedNonHumanAnimals = fosterHome.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal,
                allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals + residentNonHumanAnimal
            )

            modifyFosterHomeViewmodel.saveFosterHomeChanges(true, updatedFosterHome)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my foster home to modify_when I click to delete my foster home_then the foster home is deleted`() =
        runTest {
            val modifyFosterHomeViewmodel = getModifyFosterHomeViewmodel()

            modifyFosterHomeViewmodel.deleteFosterHome(fosterHome.id, fosterHome.ownerId)

            modifyFosterHomeViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
