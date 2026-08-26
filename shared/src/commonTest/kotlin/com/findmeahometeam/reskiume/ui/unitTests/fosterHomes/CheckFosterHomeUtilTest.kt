package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckFosterHomeUtilTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageSavedToLocalFromFosterHome = Capture.slot<(imagePath: String) -> Unit>()

    private val onModifyRemoteFosterHome = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertAcceptedNonHumanAnimalForFosterHome = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertAcceptedSecondNonHumanAnimalForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertResidentNonHumanAnimalIdForFosterHome =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertFosterHome = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertFosterHomeWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onModifyFosterHome = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyFosterHomeWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckFosterHomeUtilImpl(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForFosterHome: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.id,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        rowsDeletedOfLocalCacheArg: Int = 1,
        remoteFosterHomeReturn: Flow<RemoteFosterHome?> = flowOf(fosterHome.toData()),
        insertedFosterHomeInLocalRowsUpdatedArg: Long = 1L,
        modifiedFosterHomeInLocalRowsUpdatedArg: Int = 1,
        fosterHomeWithAllNonHumanAnimalLocalDataReturn: FosterHomeWithAllNonHumanAnimalData? = fosterHomeWithAllNonHumanAnimalData,
    ): CheckFosterHomeUtilImpl {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val konnectivity: Konnectivity = mock {
            every { isConnected } returns true
            every { currentNetworkConnection } returns NetworkConnection.WIFI
            every { isConnectedState } returns MutableStateFlow(true)
            every { currentNetworkConnectionState } returns MutableStateFlow(NetworkConnection.WIFI)
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
                    fosterHome.id,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForFosterHome

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(1) }

            everySuspend {
                deleteLocalCacheEntity(
                    fosterHome.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(rowsDeletedOfLocalCacheArg)
            }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getRemoteFosterHome(fosterHome.id)
            } returns remoteFosterHomeReturn

            everySuspend {

                modifyRemoteFosterHome(
                    fosterHome.toData(),
                    capture(onModifyRemoteFosterHome)
                )
            } calls {
                onModifyRemoteFosterHome.get()
                    .invoke(DatabaseResult.Success)
            }

            everySuspend {

                modifyRemoteFosterHome(
                    fosterHome.copy(imageUrl = "").toData(),
                    capture(onModifyRemoteFosterHome)
                )
            } calls {
                onModifyRemoteFosterHome.get()
                    .invoke(DatabaseResult.Success)
            }
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

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    user.uid,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    capture(onImageSavedToLocalFromFosterHome)
                )
            } calls {
                onImageSavedToLocalFromFosterHome.get()
                    .invoke(fosterHome.imageUrl)
            }
        }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    capture(onInsertFosterHome)
                )
            } calls {
                onInsertFosterHome.get().invoke(insertedFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                insertFosterHome(
                    fosterHome.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onInsertFosterHomeWithoutImage)
                )
            } calls {
                onInsertFosterHomeWithoutImage.get()
                    .invoke(1)
            }

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[0].toEntity(),
                    capture(onInsertAcceptedNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedNonHumanAnimalForFosterHome.get()
                    .invoke(1)
            }

            everySuspend {
                insertAcceptedNonHumanAnimalForFosterHome(
                    fosterHome.allAcceptedNonHumanAnimals[1].toEntity(),
                    capture(onInsertAcceptedSecondNonHumanAnimalForFosterHome)
                )
            } calls {
                onInsertAcceptedSecondNonHumanAnimalForFosterHome.get()
                    .invoke(1)
            }

            everySuspend {
                insertResidentNonHumanAnimalIdForFosterHome(
                    fosterHome.allResidentNonHumanAnimals[0].toEntity(),
                    capture(onInsertResidentNonHumanAnimalIdForFosterHome)
                )
            } calls {
                onInsertResidentNonHumanAnimalIdForFosterHome.get()
                    .invoke(1)
            }

            everySuspend {
                modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid).toEntity(),
                    capture(onModifyFosterHome)
                )
            } calls {
                onModifyFosterHome.get().invoke(modifiedFosterHomeInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyFosterHome(
                    fosterHome.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onModifyFosterHomeWithoutImage)
                )
            } calls {
                onModifyFosterHomeWithoutImage.get()
                    .invoke(1)
            }

            everySuspend {
                getFosterHome(fosterHome.id)
            } returns fosterHomeWithAllNonHumanAnimalLocalDataReturn
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        nonHumanAnimalState = NonHumanAnimalState.REHOMED,
                        fosterHomeId = fosterHome.id
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(1)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(
                localCacheRepository,
                log,
                konnectivity
            )

        val getFosterHomeFromRemoteRepository =
            GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val deleteCacheFromLocalRepository =
            DeleteCacheFromLocalRepository(localCacheRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertFosterHomeInLocalRepository =
            InsertFosterHomeInLocalRepository(
                localFosterHomeRepository,
                manageImagePath,
                localNonHumanAnimalRepository,
                checkNonHumanAnimalUtil,
                authRepository,
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

        val getFosterHomeFromLocalRepository =
            GetFosterHomeFromLocalRepository(localFosterHomeRepository)

        return CheckFosterHomeUtilImpl(
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getFosterHomeFromRemoteRepository,
            deleteCacheFromLocalRepository,
            downloadImageToLocalDataSource,
            insertFosterHomeInLocalRepository,
            modifyFosterHomeInLocalRepository,
            getFosterHomeFromLocalRepository,
            log
        )
    }

    @Test
    fun `given an empty cache_when the user request a foster home from remote_then foster home is retrieved and inserted in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome = null
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "insertFosterHomesInLocalRepository: Foster home ${fosterHome.id} added to local database"
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home from remote with empty avatar_then foster home is retrieved and inserted in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome = null,
                remoteFosterHomeReturn = flowOf(fosterHome.copy(imageUrl = "").toData())
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome.copy(imageUrl = ""),
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "downloadImageAndInsertFosterHomeInLocalRepository: Foster home ${fosterHome.id} has no avatar image to save locally."
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home from remote but fails inserting it in the local repo_then foster home is retrieved but not inserted in the local repo`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome = null,
                insertedFosterHomeInLocalRowsUpdatedArg = 0
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "insertFosterHomesInLocalRepository: Error adding the Foster home ${fosterHome.id} to local database"
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home but there is an issue retrieving it from the remote repo_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome = null,
                remoteFosterHomeReturn = flowOf(null)
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    null,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHomeCacheFromLocalDataSource: Foster home ${fosterHome.id} deleted in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home but there is an issue retrieving it from the remote repo and fails deleting the cache_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome = null,
                remoteFosterHomeReturn = flowOf(null),
                rowsDeletedOfLocalCacheArg = 0
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    null,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHomeCacheFromLocalDataSource: Error deleting the Foster home ${fosterHome.id} in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a foster home from remote_then foster home is retrieved and modified in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity()
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "modifyFosterHomesInLocalRepository: Foster home ${fosterHome.id} modified in local database"
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a foster home from remote with empty avatar_then foster home is retrieved and modified in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity(),
                remoteFosterHomeReturn = flowOf(fosterHome.copy(imageUrl = "").toData())
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome.copy(imageUrl = ""),
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "downloadImageAndModifyFosterHomeInLocalRepository: Foster home ${fosterHome.id} has no avatar image to save locally."
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a foster home from remote but fails modifying it in the local repo_then foster home is retrieved but not modified in the local repo`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                getLocalCacheEntityReturnForFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity(),
                modifiedFosterHomeInLocalRowsUpdatedArg = 0
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.e(
                    "CheckFosterHomeUtilImpl",
                    "modifyFosterHomesInLocalRepository: Error modifying the Foster home ${fosterHome.id} in local database"
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a foster home but there is an issue retrieving it from the remote repo_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                remoteFosterHomeReturn = flowOf(null),
                getLocalCacheEntityReturnForFosterHome =
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES,
                        timestamp = 123L
                    ).toEntity()
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    null,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckFosterHomeUtilImpl",
                    "deleteFosterHomeCacheFromLocalDataSource: Foster home ${fosterHome.id} deleted in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @Test
    fun `given recent cache_when the user request a foster home_then it is retrieved from the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl().getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome,
                    awaitItem()
                )
                awaitComplete()
            }
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${fosterHome.id} in section ${Section.FOSTER_HOMES} is up-to-date."
                )
            }
        }

    @Test
    fun `given recent cache_when the user request a foster home but there is an issue retrieving it from the local cache_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    null,
                    awaitItem()
                )
                awaitComplete()
            }
        }
}
