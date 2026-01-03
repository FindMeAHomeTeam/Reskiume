package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.ModifyNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyNonHumanAnimalViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getModifyNonHumanAnimalViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            ModifyNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath()
    ): ModifyNonHumanAnimalViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getNonHumanAnimalFromRemoteRepository =
            GetNonHumanAnimalFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val deleteCacheFromLocalRepository =
            DeleteCacheFromLocalRepository(localCacheRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertNonHumanAnimalInLocalRepository =
            InsertNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val modifyNonHumanAnimalInLocalRepository =
            ModifyNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val getNonHumanAnimalFromLocalRepository =
            GetNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val modifyNonHumanAnimalInRemoteRepository =
            ModifyNonHumanAnimalInRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        val deleteNonHumanAnimalFromRemoteRepository =
            DeleteNonHumanAnimalFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val deleteNonHumanAnimalFromLocalRepository =
            DeleteNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

        val getCompleteImagePathFromLocalDataSource =
            GetCompleteImagePathFromLocalDataSource(manageImagePath)

        val checkNonHumanAnimalUtil = CheckNonHumanAnimalUtil(
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getNonHumanAnimalFromRemoteRepository,
            deleteCacheFromLocalRepository,
            downloadImageToLocalDataSource,
            insertNonHumanAnimalInLocalRepository,
            modifyNonHumanAnimalInLocalRepository,
            getNonHumanAnimalFromLocalRepository,
            getCompleteImagePathFromLocalDataSource,
            log
        )

        val deleteNonHumanAnimalUtil = DeleteNonHumanAnimalUtil(
            getNonHumanAnimalFromRemoteRepository,
            getNonHumanAnimalFromLocalRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            deleteNonHumanAnimalFromRemoteRepository,
            deleteNonHumanAnimalFromLocalRepository,
            deleteCacheFromLocalRepository,
            log
        )

        return ModifyNonHumanAnimalViewmodel(
            saveStateHandleProvider,
            checkNonHumanAnimalUtil,
            deleteNonHumanAnimalUtil,
            getNonHumanAnimalFromRemoteRepository,
            deleteImageFromRemoteDataSource,
            getNonHumanAnimalFromLocalRepository,
            deleteImageFromLocalDataSource,
            uploadImageToRemoteDataSource,
            modifyNonHumanAnimalInRemoteRepository,
            modifyNonHumanAnimalInLocalRepository,
            modifyCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given a user with empty cache_when the app downloads the data to modify a non human animal_then the non human animal is saved in local cache and displayed with their local image`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "", imageUrl = "${nonHumanAnimal.caregiverId}${nonHumanAnimal.id}.webp")), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with empty cache and a wrong NHA id_when the app downloads the data to modify a NHA_then the app display an error and deletes the stored cache entity`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    ModifyNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with an outdated cache_when the app downloads the data to modify a non human animal_then the non human animal is updated in the local cache and displayed with the local image`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "", imageUrl = "${nonHumanAnimal.caregiverId}${nonHumanAnimal.id}.webp")), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with an outdated cache and a wrong NHA id_when the app downloads the data to modify a NHA_then the app display an error and deletes the stored cache entity`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = "wrongId",
                            section = Section.NON_HUMAN_ANIMALS,
                            timestamp = 123L
                        ).toEntity()
                    )
                ),
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    ModifyNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with recent cache_when the app gets the data to modify a non human animal_then the data is retrieved from the local cache and displayed`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with recent cache and a wrong NHA id_when the app gets the data to modify a non human animal_then the data is not retrieved from the local cache`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = "wrongId",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    ModifyNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a modified non human animal_when the app updates the data_then the data is modified in local and remote repositories`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = true,
                modifiedNonHumanAnimal = nonHumanAnimal
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a modified non human animal with wrong id_when the app updates the data_then the data is not modified`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = true,
                modifiedNonHumanAnimal = nonHumanAnimal.copy(id = "wrongId")
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a modified non human animal_when the app updates the data and fails deleting the remote image_then the data is not modified`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = true,
                modifiedNonHumanAnimal = nonHumanAnimal
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a modified non human animal_when the app updates the data and fails deleting the local image_then the data is not modified`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = true,
                modifiedNonHumanAnimal = nonHumanAnimal
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a modified non human animal with no image_when the app updates the data_then the data is modified in the local and remote repositories`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(
                        nonHumanAnimal.copy(imageUrl = "").toData()
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(
                        nonHumanAnimal.copy(imageUrl = "").toEntity()
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = true,
                modifiedNonHumanAnimal = nonHumanAnimal.copy(imageUrl = "")
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a modified non human animal_when the app updates the data but fails in the local repository_then the data is not modified`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = false,
                modifiedNonHumanAnimal = nonHumanAnimal
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a non human animal_when the app deletes the non human animal_then the data is deleted from the local and remote repositories`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                )
            )
            modifyNonHumanAnimalViewmodel.deleteNonHumanAnimal(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a non human animal_when the app deletes the non human animal but fails deleting its cache_then the data is deleted from the local and remote repositories and LogE is called`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    localNonHumanAnimalList = mutableListOf(nonHumanAnimal.toEntity())
                )
            )
            modifyNonHumanAnimalViewmodel.deleteNonHumanAnimal(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
