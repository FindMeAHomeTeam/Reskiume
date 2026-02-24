package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteFosterHomeUtilTest : CoroutineTestDispatcher() {

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageDeletedFromRemoteForFosterHome = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onImageDeletedFromLocalForFosterHome = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onModifyLocalNonHumanAnimal = Capture.slot<(rowsModified: Int) -> Unit>()

    private val onDeleteRemoteFosterHome = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteFosterHomeFromLocalRepository =
        Capture.slot<suspend (rowsDeleted: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getDeleteFosterHomeUtil(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForFosterHome: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = fosterHome.id,
                section = Section.FOSTER_HOMES
            ).toEntity(),
        numberOfRowsDeletedInLocalCacheArg: Int = 1,
        remoteFosterHomeReturn: Flow<RemoteFosterHome> = flowOf(fosterHome.toData()),
        databaseResultOfDeletingFosterHomesInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        isRemoteImageDeletedFlagForFosterHome: Boolean = true,
        isLocalImageDeletedFlagForFosterHome: Boolean = true,
        fosterHomeWithAllNonHumanAnimalLocalDataReturn: FosterHomeWithAllNonHumanAnimalData? = fosterHomeWithAllNonHumanAnimalData,
        modifiedNonHumanAnimalsInLocalRepositoryArg: Int = 1,
        rowsOfFosterHomesDeletedFromLocalRepositoryArg: Int = 1
    ): DeleteFosterHomeUtilImpl {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    fosterHome.id,
                    Section.FOSTER_HOMES
                )
            } returns getLocalCacheEntityReturnForFosterHome

            everySuspend {
                deleteLocalCacheEntity(
                    fosterHome.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(numberOfRowsDeletedInLocalCacheArg)
            }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getRemoteFosterHome(fosterHome.id)
            } returns remoteFosterHomeReturn

            everySuspend {
                deleteRemoteFosterHome(
                    fosterHome.id,
                    fosterHome.ownerId,
                    capture(onDeleteRemoteFosterHome)
                )
            } calls {
                onDeleteRemoteFosterHome.get()
                    .invoke(databaseResultOfDeletingFosterHomesInRemoteRepositoryArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id + "789", nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }
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
                    nonHumanAnimal.id + "789",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "789"))
        }

        val storageRepository: StorageRepository = mock {

            everySuspend {
                deleteRemoteImage(
                    user.uid,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    capture(onImageDeletedFromRemoteForFosterHome)
                )
            } calls {
                onImageDeletedFromRemoteForFosterHome.get()
                    .invoke(isRemoteImageDeletedFlagForFosterHome)
            }

            every {
                deleteLocalImage(
                    fosterHome.imageUrl,
                    capture(onImageDeletedFromLocalForFosterHome)
                )
            } calls {
                onImageDeletedFromLocalForFosterHome.get()
                    .invoke(isLocalImageDeletedFlagForFosterHome)
            }
        }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

            everySuspend {
                getFosterHome(fosterHome.id)
            } returns fosterHomeWithAllNonHumanAnimalLocalDataReturn

            everySuspend {
                deleteFosterHome(
                    fosterHome.id,
                    capture(onDeleteFosterHomeFromLocalRepository)
                )
            } calls {
                onDeleteFosterHomeFromLocalRepository.get()
                    .invoke(rowsOfFosterHomesDeletedFromLocalRepositoryArg)
            }
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))

            everySuspend {
                getNonHumanAnimal(nonHumanAnimal.id)
            } returns nonHumanAnimal.toEntity()

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onModifyLocalNonHumanAnimal)
                )
            } calls {
                onModifyLocalNonHumanAnimal.get().invoke(modifiedNonHumanAnimalsInLocalRepositoryArg)
            }
        }

        val getFosterHomeFromRemoteRepository =
            GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val getFosterHomeFromLocalRepository =
            GetFosterHomeFromLocalRepository(localFosterHomeRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val deleteMyFosterHomeFromRemoteRepository =
            DeleteMyFosterHomeFromRemoteRepository(
                authRepository,
                fireStoreRemoteFosterHomeRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

        val deleteMyFosterHomeFromLocalRepository =
            DeleteMyFosterHomeFromLocalRepository(
                localFosterHomeRepository,
                localNonHumanAnimalRepository,
                checkNonHumanAnimalUtil,
                log
            )

        val deleteCacheFromLocalRepository =
            DeleteCacheFromLocalRepository(localCacheRepository)

        return DeleteFosterHomeUtilImpl(
            getFosterHomeFromRemoteRepository,
            getFosterHomeFromLocalRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            deleteMyFosterHomeFromRemoteRepository,
            deleteMyFosterHomeFromLocalRepository,
            deleteCacheFromLocalRepository,
            log
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home_then the foster home is deleted`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil()

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.d(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeCacheFromLocalDataSource: Foster home ${fosterHome.id} deleted in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the current image from the remote datasource_then the app displays an error`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                isRemoteImageDeletedFlagForFosterHome = false
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the foster home ${fosterHome.id} in the remote data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails retrieving the local foster home trying to delete its local image_then the app displays an error`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                fosterHomeWithAllNonHumanAnimalLocalDataReturn = null
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the foster home ${fosterHome.id} in the local data source because the local foster home does not exist"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the current image from the local datasource_then the app displays an error`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                isLocalImageDeletedFlagForFosterHome = false
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the foster home ${fosterHome.id} in the local data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the foster home from the remote datasource_then the app displays an error`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                databaseResultOfDeletingFosterHomesInRemoteRepositoryArg = DatabaseResult.Error()
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeFromRemoteDataSource: Error deleting the foster home ${fosterHome.id} in the remote data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the foster home from the local datasource_then the app displays an error`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                rowsOfFosterHomesDeletedFromLocalRepositoryArg = 0
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeFromLocalDataSource: Error deleting the foster home ${fosterHome.id} in the local data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the foster home from the local cache_then the deletion process is finished but the local cache is not deleted`() =
        runTest {
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                numberOfRowsDeletedInLocalCacheArg = 0
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                {},
                {}
            )

            runCurrent()

            verify {
                log.e(
                    "DeleteFosterHomeUtil",
                    "deleteFosterHomeCacheFromLocalDataSource: Error deleting the foster home ${fosterHome.id} in the local cache in section ${Section.FOSTER_HOMES}"
                )
            }
        }
}
