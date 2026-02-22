package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteNonHumanAnimal
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
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtilImpl
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.ModifyNonHumanAnimalViewmodel
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
import kotlin.test.assertTrue

class ModifyNonHumanAnimalViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDelete: Int) -> Unit>()

    private val onDeleteLocalCacheEntityWithWrongId = Capture.slot<(rowsDelete: Int) -> Unit>()

    private val onDownloadImageToLocal = Capture.slot<(imagePath: String) -> Unit>()

    private val onUploadImageToLocal = Capture.slot<(imagePath: String) -> Unit>()

    private val onImageDeletedFromRemote = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onImageDeletedFromLocal = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onInsertNonHumanAnimalInLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyNonHumanAnimalInRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteNonHumanAnimalFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyNonHumanAnimalInLocal = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteNonHumanAnimalFromLocal = Capture.slot<(rowsDeleted: Int) -> Unit>()

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

    private fun getModifyNonHumanAnimalViewmodel(
        nonHumanAnimalIdArg: String = nonHumanAnimal.id,
        getLocalCacheEntityForNonHumanAnimalReturn: LocalCacheEntity? =
            localCache.copy(cachedObjectId = nonHumanAnimal.id, section = Section.NON_HUMAN_ANIMALS)
                .toEntity(),
        getLocalCacheEntityForWrongNonHumanAnimalReturn: LocalCacheEntity? = null,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        numberOfNonHumanAnimalsDeletedFromLocalCacheArg: Int = 1,
        numberOfNonHumanAnimalsDeletedFromLocalCacheWithWrongIdArg: Int = 1,
        databaseResultAfterModifyingRemoteNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        databaseResultAfterDeletingRemoteNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        remoteNonHumanAnimalArg: RemoteNonHumanAnimal = nonHumanAnimal.toData(),
        remoteImageDeletedArg: Boolean = true,
        localImageDeletedArg: Boolean = true,
        nonHumanAnimalEntityReturn: NonHumanAnimalEntity? = nonHumanAnimal.toEntity(),
        nonHumanAnimalEntityArg: NonHumanAnimalEntity = nonHumanAnimal.toEntity(),
        rowsUpdatedNonHumanAnimalArg: Int = 1,
        rowsDeletedOfNonHumanAnimalsArg: Int = 1
    ): ModifyNonHumanAnimalViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<ModifyNonHumanAnimal>(any(), any())
            } returns ModifyNonHumanAnimal(nonHumanAnimalIdArg, nonHumanAnimal.caregiverId)
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

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        remoteNonHumanAnimalArg,
                        capture(onModifyNonHumanAnimalInRemote)
                    )
                } calls {
                    onModifyNonHumanAnimalInRemote.get()
                        .invoke(databaseResultAfterModifyingRemoteNonHumanAnimalArg)
                }

                every {
                    deleteRemoteNonHumanAnimal(
                        nonHumanAnimal.id,
                        nonHumanAnimal.caregiverId,
                        capture(onDeleteNonHumanAnimalFromRemote)
                    )
                } calls {
                    onDeleteNonHumanAnimalFromRemote.get()
                        .invoke(databaseResultAfterDeletingRemoteNonHumanAnimalArg)
                }
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

            every {
                uploadImage(
                    nonHumanAnimal.caregiverId,
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS,
                    nonHumanAnimal.imageUrl,
                    capture(onUploadImageToLocal)
                )
            } calls { onUploadImageToLocal.get().invoke(nonHumanAnimal.imageUrl) }

            everySuspend {
                deleteRemoteImage(
                    nonHumanAnimal.caregiverId,
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS,
                    capture(onImageDeletedFromRemote)
                )
            } calls { onImageDeletedFromRemote.get().invoke(remoteImageDeletedArg) }

            every {
                deleteLocalImage(
                    nonHumanAnimal.imageUrl,
                    capture(onImageDeletedFromLocal)
                )
            } calls { onImageDeletedFromLocal.get().invoke(localImageDeletedArg) }
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

            everySuspend {
                deleteNonHumanAnimal(
                    nonHumanAnimal.id,
                    capture(onDeleteNonHumanAnimalFromLocal)
                )
            } calls {
                onDeleteNonHumanAnimalFromLocal.get().invoke(rowsDeletedOfNonHumanAnimalsArg)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
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
            InsertNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

        val modifyNonHumanAnimalInLocalRepository =
            ModifyNonHumanAnimalInLocalRepository(manageImagePath, localNonHumanAnimalRepository, authRepository)

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

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

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

        return ModifyNonHumanAnimalViewmodel(
            saveStateHandleProvider,
            checkNonHumanAnimalUtil,
            getImagePathForFileNameFromLocalDataSource,
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
    fun `given a user with empty cache_when the app downloads the data to modify a non human animal_then the non human animal is saved in local cache and displayed`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                getLocalCacheEntityForNonHumanAnimalReturn = null
            ).nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal.copy(savedBy = "")), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with empty cache and a wrong NHA id_when the app downloads the data to modify a NHA_then the app shows nothing`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForNonHumanAnimalReturn = null
            ).nonHumanAnimalFlow.test {
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "wrongId added to local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @Test
    fun `given a user with an outdated cache_when the app downloads the data to modify a non human animal_then the non human animal is updated in local cache and displayed`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
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
    fun `given a user with an outdated cache and a wrong NHA id_when the app downloads the data to modify a NHA_then the app displays nothing`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForWrongNonHumanAnimalReturn = localCache.copy(
                    cachedObjectId = "wrongId",
                    section = Section.NON_HUMAN_ANIMALS,
                    timestamp = 123L
                ).toEntity()
            ).nonHumanAnimalFlow.test {
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "wrongId updated in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @Test
    fun `given a user with recent cache_when the app gets the data to modify a non human animal_then the data is retrieved from the local cache and displayed`() =
        runTest {
            getModifyNonHumanAnimalViewmodel().nonHumanAnimalFlow.test {
                assertEquals(UiState.Success(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with recent cache and a wrong NHA id_when the app gets the data to modify a non human animal_then the data is not retrieved from the local cache`() =
        runTest {
            getModifyNonHumanAnimalViewmodel(
                nonHumanAnimalIdArg = "wrongId",
                getLocalCacheEntityForWrongNonHumanAnimalReturn = localCache.copy(
                    cachedObjectId = "wrongId",
                    section = Section.NON_HUMAN_ANIMALS
                ).toEntity(),
                nonHumanAnimalEntityReturn = null
            ).nonHumanAnimalFlow.test {
                awaitComplete()
            }

            runCurrent()

            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for wrongId in section ${Section.NON_HUMAN_ANIMALS} is up-to-date."
                )
                log.d(
                    "CheckNonHumanAnimalUtilImpl",
                    "deleteNonHumanAnimalCacheFromLocalDataSource: Non human animal wrongId deleted in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @Test
    fun `given a modified non human animal_when the app updates the data_then the data is modified in local and remote repositories`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel()
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
                nonHumanAnimalIdArg = "wrongId"
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
                remoteImageDeletedArg = false
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
    fun `given a modified non human animal_when the app updates the data and fails deleting the local image_then the data is not modified in the local repository`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                localImageDeletedArg = false
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
                remoteNonHumanAnimalArg = nonHumanAnimal.copy(imageUrl = "").toData(),
                nonHumanAnimalEntityArg = nonHumanAnimal.copy(imageUrl = "").toEntity(),
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
    fun `given a modified non human animal_when the app updates the data but fails in the remote repository_then the data is not modified`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                databaseResultAfterModifyingRemoteNonHumanAnimalArg = DatabaseResult.Error()
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
    fun `given a modified non human animal_when the app updates the data but fails in the local repository_then the data is not modified in the local repository`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                rowsUpdatedNonHumanAnimalArg = 0
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a modified non human animal_when the app updates the data but fails updating the cache_then the data is modified in the local and remote repositories and logE is called`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                localCacheUpdatedInLocalDatasourceArg = 0
            )
            modifyNonHumanAnimalViewmodel.saveNonHumanAnimalChanges(
                isDifferentImage = false,
                modifiedNonHumanAnimal = nonHumanAnimal
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }

            runCurrent()

            verify {
                log.e(
                    "ModifyNonHumanAnimalViewModel",
                    "Error updating ${nonHumanAnimal.id} in local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }

    @Test
    fun `given a non human animal_when the app deletes the non human animal_then the data is deleted from the local and remote repositories`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel()
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
    fun `given a non human animal_when the app deletes the non human animal but fails deleting them in the remote repository_then the data is not deleted`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                databaseResultAfterDeletingRemoteNonHumanAnimalArg = DatabaseResult.Error()
            )
            modifyNonHumanAnimalViewmodel.deleteNonHumanAnimal(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a non human animal_when the app deletes the non human animal but fails deleting them in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                rowsDeletedOfNonHumanAnimalsArg = 0
            )
            modifyNonHumanAnimalViewmodel.deleteNonHumanAnimal(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId
            )
            modifyNonHumanAnimalViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a non human animal_when the app deletes the non human animal but fails deleting its cache_then the data is deleted from the local and remote repositories and LogE is called`() =
        runTest {
            val modifyNonHumanAnimalViewmodel = getModifyNonHumanAnimalViewmodel(
                numberOfNonHumanAnimalsDeletedFromLocalCacheArg = 0
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

            runCurrent()

            verify {
                log.e(
                    "DeleteNonHumanAnimalUtilImpl",
                    "Error deleting the non human animal ${nonHumanAnimal.id} in the local cache in section ${Section.NON_HUMAN_ANIMALS}"
                )
            }
        }
}
