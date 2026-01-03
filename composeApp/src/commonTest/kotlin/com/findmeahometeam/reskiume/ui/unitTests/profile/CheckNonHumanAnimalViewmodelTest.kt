package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckNonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckNonHumanAnimalViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDelete: Int) -> Unit>()

    private val onDeleteLocalCacheEntityWithWrongId = Capture.slot<(rowsDelete: Int) -> Unit>()

    private val onDownloadImageToLocal = Capture.slot<(imagePath: String) -> Unit>()

    private val onInsertNonHumanAnimalInLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyNonHumanAnimalInLocal = Capture.slot<(rowsUpdated: Int) -> Unit>()

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

    private fun getCheckNonHumanAnimalViewmodel(
        nonHumanAnimalIdArg: String = nonHumanAnimal.id,
        getLocalCacheEntityForNonHumanAnimalReturn: LocalCacheEntity? =
            localCache.copy(cachedObjectId = nonHumanAnimal.id, section = Section.NON_HUMAN_ANIMALS)
                .toEntity(),
        getLocalCacheEntityForWrongNonHumanAnimalReturn: LocalCacheEntity? = null,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        numberOfNonHumanAnimalsDeletedFromLocalCacheArg: Int = 1,
        numberOfNonHumanAnimalsDeletedFromLocalCacheWithWrongIdArg: Int = 1,
        nonHumanAnimalEntityReturn: NonHumanAnimalEntity? = nonHumanAnimal.toEntity(),
        nonHumanAnimalEntityArg: NonHumanAnimalEntity = nonHumanAnimal.toEntity(),
        rowsUpdatedNonHumanAnimalArg: Int = 1
    ): CheckNonHumanAnimalViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckNonHumanAnimal>(any(), any())
            } returns CheckNonHumanAnimal(nonHumanAnimalIdArg, nonHumanAnimal.caregiverId)
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authUser))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(1L)
            }

            everySuspend {
                getLocalCacheEntity(
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS
                )
            } returns getLocalCacheEntityForNonHumanAnimalReturn

            everySuspend {
                getLocalCacheEntity(
                    "wrongId",
                    Section.NON_HUMAN_ANIMALS
                )
            } returns getLocalCacheEntityForWrongNonHumanAnimalReturn

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }

            everySuspend {
                deleteLocalCacheEntity(
                    nonHumanAnimal.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(numberOfNonHumanAnimalsDeletedFromLocalCacheArg)
            }

            everySuspend {
                deleteLocalCacheEntity(
                    "wrongId",
                    capture(onDeleteLocalCacheEntityWithWrongId)
                )
            } calls {
                onDeleteLocalCacheEntityWithWrongId.get()
                    .invoke(numberOfNonHumanAnimalsDeletedFromLocalCacheWithWrongIdArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                every {
                    getRemoteNonHumanAnimal(
                        nonHumanAnimal.id,
                        nonHumanAnimal.caregiverId
                    )
                } returns flowOf(nonHumanAnimal.toData())

                every {
                    getRemoteNonHumanAnimal(
                        "wrongId",
                        nonHumanAnimal.caregiverId
                    )
                } returns flowOf(null)
            }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    nonHumanAnimal.caregiverId,
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS,
                    capture(onDownloadImageToLocal)
                )
            } calls { onDownloadImageToLocal.get().invoke(nonHumanAnimal.imageUrl) }
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {
            everySuspend {
                insertNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onInsertNonHumanAnimalInLocal)
                )
            } calls {
                onInsertNonHumanAnimalInLocal.get().invoke(1L)
            }

            everySuspend {
                getNonHumanAnimal(nonHumanAnimalIdArg)
            } returns nonHumanAnimalEntityReturn

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimalEntityArg,
                    capture(onModifyNonHumanAnimalInLocal)
                )
            } calls { onModifyNonHumanAnimalInLocal.get().invoke(rowsUpdatedNonHumanAnimalArg) }
        }

        val manageImagePath: ManageImagePath = mock {
            every { getCompleteImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl
        }

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

        return CheckNonHumanAnimalViewmodel(
            saveStateHandleProvider,
            checkNonHumanAnimalUtil
        )
    }

    @Test
    fun `given a user with empty cache_when the app downloads the data to check a non human animal_then the non human animal is saved in local cache and displayed`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                getLocalCacheEntityForNonHumanAnimalReturn = null
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with empty cache and a wrong NHA id_when the app downloads the data to check a NHA_then the app display an error and deletes the stored cache entity`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForNonHumanAnimalReturn = null
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "wrongId added to local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
                log.d(
                    "CheckNonHumanAnimalUtil",
                    "Non human animal wrongId deleted in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given no cache and a wrong NHA id_when the app gets the data to check a NHA but there is an error in the local db_then the app shows an error and do not delete the stored cache entity`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForNonHumanAnimalReturn = null,
                numberOfNonHumanAnimalsDeletedFromLocalCacheWithWrongIdArg = 0
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "wrongId added to local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
                log.e(
                    "CheckNonHumanAnimalUtil",
                    "Error deleting the non human animal wrongId in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @Test
    fun `given a user with an outdated cache_when the app downloads the data to check a non human animal_then the non human animal is updated in local cache and displayed`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                getLocalCacheEntityForNonHumanAnimalReturn = localCache.copy(
                    cachedObjectId = nonHumanAnimal.id,
                    section = Section.NON_HUMAN_ANIMALS,
                    timestamp = 123L
                ).toEntity()
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with an outdated cache and a wrong NHA id_when the app downloads the data to check a NHA_then the app display an error and deletes the stored cache entity`() =
        runTest {
            getCheckNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForWrongNonHumanAnimalReturn = localCache.copy(
                    cachedObjectId = "wrongId",
                    section = Section.NON_HUMAN_ANIMALS,
                    timestamp = 123L
                ).toEntity()
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Error(), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "wrongId updated in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
                log.d(
                    "CheckNonHumanAnimalUtil",
                    "Non human animal wrongId deleted in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }
}
