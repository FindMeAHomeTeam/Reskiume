package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllNonHumanAnimals
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkAllNonHumanAnimals.CheckAllNonHumanAnimalsViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllNonHumanAnimalsViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllNonHumanAnimalsViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider =
            FakeSaveStateHandleProvider(CheckAllNonHumanAnimals(user.uid)),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        konnectivity: Konnectivity = FakeKonnectivity()
    ): CheckAllNonHumanAnimalsViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getAllNonHumanAnimalsFromRemoteRepository =
            GetAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertNonHumanAnimalInLocalRepository =
            InsertNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val insertCacheInLocalRepository = InsertCacheInLocalRepository(localCacheRepository)

        val modifyNonHumanAnimalInLocalRepository =
            ModifyNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val modifyCacheInLocalRepository = ModifyCacheInLocalRepository(localCacheRepository)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        return CheckAllNonHumanAnimalsViewmodel(
            saveStateHandleProvider,
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllNonHumanAnimalsFromRemoteRepository,
            downloadImageToLocalDataSource,
            insertNonHumanAnimalInLocalRepository,
            insertCacheInLocalRepository,
            modifyNonHumanAnimalInLocalRepository,
            modifyCacheInLocalRepository,
            getAllNonHumanAnimalsFromLocalRepository,
            log
        )
    }

    @Test
    fun `given a user with empty cache_when the user clicks on a non human animal_then the non human animal is saved in local cache and displayed`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel(
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                )
            ).nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

        @Test
        fun `given a registered NHA with no avatar with empty cache_when the user clicks on a NHA but have an error saving them_then the NHA is shown but not saved in local cache`() =
            runTest {
                getCheckAllNonHumanAnimalsViewmodel(
                    realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                        remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.copy(imageUrl = "").toData())
                    ),
                    localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(mutableListOf(nonHumanAnimal.toEntity()))
                ).nonHumanAnimalListFlow.test {
                    assertEquals(listOf(nonHumanAnimal.copy(imageUrl = "", savedBy = "")), awaitItem())
                    awaitComplete()
                }
            }

        @Test
        fun `given a user with an outdated local cache_when the user clicks on a non human animal_then the non human animal is modified in local cache and displayed`() =
            runTest {
                getCheckAllNonHumanAnimalsViewmodel(
                    localCacheRepository = FakeLocalCacheRepository(
                        localCacheList = mutableListOf(
                            localCache.copy(section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity(),
                            localCache.copy(uid = nonHumanAnimal.id, section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity()
                        )
                    ),
                    realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                        remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.toData())
                    ),
                    localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(mutableListOf(nonHumanAnimal.toEntity()))
                ).nonHumanAnimalListFlow.test {
                    assertEquals(listOf(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                    awaitComplete()
                }
            }

        @Test
        fun `given a registered NHA with no avatar and outdated cache_when the user clicks on a NHA but have an error modifying them_then the NHA is shown but not modified in local cache`() =
            runTest {
                getCheckAllNonHumanAnimalsViewmodel(
                    localCacheRepository = FakeLocalCacheRepository(
                        localCacheList = mutableListOf(
                            localCache.copy(section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity(),
                            localCache.copy(uid = nonHumanAnimal.id, section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity()
                        )
                    ),
                    realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                        remoteNonHumanAnimalList = mutableListOf(nonHumanAnimal.copy(imageUrl = "").toData())
                    )
                ).nonHumanAnimalListFlow.test {
                    assertEquals(listOf(nonHumanAnimal.copy(imageUrl = "", savedBy = "")), awaitItem())
                    awaitComplete()
                }
            }

        @Test
        fun `given a registered non human animal with recent local cache_when the user clicks on check non human animals_then the non human animal is retrieved from local cache and displayed`() =
            runTest {
                getCheckAllNonHumanAnimalsViewmodel(
                    localCacheRepository = FakeLocalCacheRepository(
                        localCacheList = mutableListOf(
                            localCache.copy(section = Section.NON_HUMAN_ANIMALS).toEntity(),
                            localCache.copy(uid = nonHumanAnimal.id, section = Section.NON_HUMAN_ANIMALS).toEntity()
                        )
                    ),
                    localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(mutableListOf(nonHumanAnimal.toEntity()))
                ).nonHumanAnimalListFlow.test {
                    assertEquals(listOf(nonHumanAnimal), awaitItem())
                    awaitComplete()
                }
            }
}
