package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
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
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.review
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllNonHumanAnimals
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllNonHumanAnimals.CheckAllNonHumanAnimalsViewmodel
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllNonHumanAnimalsViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onSaveImageToLocal = Capture.slot<(String) -> Unit>()

    private val onInsertNonHumanAnimalFromLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyNonHumanAnimalFromLocal = Capture.slot<(Int) -> Unit>()

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

    private fun getCheckAllNonHumanAnimalsViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturn: LocalCacheEntity? =
            localCache.copy(section = Section.NON_HUMAN_ANIMALS).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        getAllRemoteNonHumanAnimalsReturn: Flow<List<RemoteNonHumanAnimal>> = flowOf(listOf(nonHumanAnimal.toData())),
        absolutePathArg: String = nonHumanAnimal.imageUrl,
        nonHumanAnimalIdInsertedInLocalDatasourceArg: Long = 1L,
        rowsUpdatedNonHumanAnimalArg: Int = 1
    ): CheckAllNonHumanAnimalsViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckAllNonHumanAnimals>(any(), any())
            } returns CheckAllNonHumanAnimals(user.uid)
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.NON_HUMAN_ANIMALS
                )
            } returns getLocalCacheEntityReturn

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

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = mock {
            every {
                getAllRemoteNonHumanAnimals(review.reviewedUid)
            } returns getAllRemoteNonHumanAnimalsReturn
        }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    user.uid,
                    nonHumanAnimal.id.toString(),
                    Section.NON_HUMAN_ANIMALS,
                    capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathArg) }
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onInsertNonHumanAnimalFromLocal)
                )
            } calls { onInsertNonHumanAnimalFromLocal.get().invoke(nonHumanAnimalIdInsertedInLocalDatasourceArg) }

            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.copy(imageUrl = "").toEntity(),
                    capture(onInsertNonHumanAnimalFromLocal)
                )
            } calls { onInsertNonHumanAnimalFromLocal.get().invoke(nonHumanAnimalIdInsertedInLocalDatasourceArg) }


            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onModifyNonHumanAnimalFromLocal)
                )
            } calls { onModifyNonHumanAnimalFromLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(imageUrl = "").toEntity(),
                    capture(onModifyNonHumanAnimalFromLocal)
                )
            } calls { onModifyNonHumanAnimalFromLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }

            every {
                getAllNonHumanAnimals(user.uid)
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))
        }

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

        val modifyNonHumanAnimalInLocalRepository =
            ModifyNonHumanAnimalInLocalRepository(localNonHumanAnimalRepository, authRepository)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        return CheckAllNonHumanAnimalsViewmodel(
            saveStateHandleProvider,
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getAllNonHumanAnimalsFromRemoteRepository,
            downloadImageToLocalDataSource,
            insertNonHumanAnimalInLocalRepository,
            modifyNonHumanAnimalInLocalRepository,
            getAllNonHumanAnimalsFromLocalRepository,
            log
        )
    }

    @Test
    fun `given a user with empty cache_when the user clicks on a non human animal_then the non human animal is saved in local cache and displayed`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel(
                getLocalCacheEntityReturn = null
            ).nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered NHA with no avatar with empty cache_when the user clicks on a NHA but have an error saving them_then the NHA is shown but not saved in local cache`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel(
                getLocalCacheEntityReturn = null,
                getAllRemoteNonHumanAnimalsReturn = flowOf(listOf(nonHumanAnimal.copy(imageUrl = "").toData())),
                nonHumanAnimalIdInsertedInLocalDatasourceArg = 0
            ).nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal.copy(imageUrl = "", savedBy = "")), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.e(
                    "CheckNonHumanAnimalsViewmodel",
                    "Error adding the non human animal ${nonHumanAnimal.id} to local database"
                )
            }
        }

    @Test
    fun `given a user with an outdated local cache_when the user clicks on a non human animal_then the non human animal is modified in local cache and displayed`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel(
                getLocalCacheEntityReturn =
                    localCache.copy(section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity()
            ).nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered NHA with no avatar and outdated cache_when the user clicks on a NHA but have an error modifying them_then the NHA is shown but not modified in local cache`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel(
                getLocalCacheEntityReturn =
                    localCache.copy(section = Section.NON_HUMAN_ANIMALS, timestamp = 123L).toEntity(),
                getAllRemoteNonHumanAnimalsReturn = flowOf(listOf(nonHumanAnimal.copy(imageUrl = "").toData())),
                rowsUpdatedNonHumanAnimalArg = 0
            ).nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal.copy(imageUrl = "", savedBy = "")), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.e(
                    "CheckNonHumanAnimalsViewmodel",
                    "Error modifying the non human animal ${nonHumanAnimal.id} in local database"
                )
            }
        }

    @Test
    fun `given a registered non human animal with recent local cache_when the user clicks on check non human animals_then the non human animal is retrieved from local cache and displayed`() =
        runTest {
            getCheckAllNonHumanAnimalsViewmodel().nonHumanAnimalListFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }
}
