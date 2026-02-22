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
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckNonHumanAnimal
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
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckNonHumanAnimalViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckNonHumanAnimalViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            CheckNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath()
    ): CheckNonHumanAnimalViewmodel {

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
            InsertNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

        val modifyNonHumanAnimalInLocalRepository =
            ModifyNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

        val getNonHumanAnimalFromLocalRepository =
            GetNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val deleteNonHumanAnimalFromRemoteRepository =
            DeleteNonHumanAnimalFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val deleteNonHumanAnimalFromLocalRepository =
            DeleteNonHumanAnimalFromLocalRepository(localNonHumanAnimalRepository)

        val deleteNonHumanAnimalUtil = DeleteNonHumanAnimalUtilImpl(
            getNonHumanAnimalFromRemoteRepository,
            getNonHumanAnimalFromLocalRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            deleteNonHumanAnimalFromRemoteRepository,
            deleteNonHumanAnimalFromLocalRepository,
            deleteCacheFromLocalRepository,
            log
        )

        val checkNonHumanAnimalUtil = CheckNonHumanAnimalUtilImpl(
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getNonHumanAnimalFromRemoteRepository,
            deleteNonHumanAnimalUtil,
            deleteCacheFromLocalRepository,
            downloadImageToLocalDataSource,
            insertNonHumanAnimalInLocalRepository,
            modifyNonHumanAnimalInLocalRepository,
            getNonHumanAnimalFromLocalRepository,
            log
        )

        return CheckNonHumanAnimalViewmodel(
            saveStateHandleProvider,
            checkNonHumanAnimalUtil,
            getImagePathForFileNameFromLocalDataSource
        )
    }

    @Test
    fun `given a user with empty cache_when the app downloads the data to check a non human animal_then the non human animal is saved in local cache and displayed with their local image`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "", imageUrl = "${nonHumanAnimal.caregiverId}${nonHumanAnimal.id}.webp")), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with an outdated cache_when the app downloads the data to check a non human animal_then the non human animal is updated in local cache and displayed with their local image`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
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
    fun `given a user with an outdated cache and a wrong NHA id_when the app downloads the data to check a NHA_then the app displays nothing and deletes the stored cache entity`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.copy(id = "wrongId").toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = "wrongId",
                            section = Section.NON_HUMAN_ANIMALS,
                            timestamp = 123L
                        ).toEntity()
                    )
                ),
                storageRepository = FakeStorageRepository(
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                )
            ).nonHumanAnimalFlow.test {
                awaitComplete()
            }
        }

    @Test
    fun `given a user with recent cache_when the app gets the data to check a non human animal_then the data is retrieved from the local cache and displayed`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
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
    fun `given a user with recent cache and a wrong NHA id_when the app gets the data to check a non human animal_then the data is not retrieved from the local cache`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = "wrongId",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                )
            ).nonHumanAnimalFlow.test {
                awaitComplete()
            }
        }
}
