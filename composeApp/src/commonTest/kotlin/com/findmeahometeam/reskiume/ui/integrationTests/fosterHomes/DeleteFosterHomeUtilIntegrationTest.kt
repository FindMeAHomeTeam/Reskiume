package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.DeleteFosterHomeUtilImpl
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteFosterHomeUtilIntegrationTest : CoroutineTestDispatcher() {

    private fun getDeleteFosterHomeUtil(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = FakeDeleteNonHumanAnimalUtil(
            mutableListOf(nonHumanAnimal, nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        ),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        ),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity(),
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
            )
        ),
        log: Log = FakeLog()
    ): DeleteFosterHomeUtilImpl {

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
            val fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                remoteFosterHomeList = mutableListOf(fosterHome.toData())
            )
            val localFosterHomeRepository = FakeLocalFosterHomeRepository(
                localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                    fosterHomeWithAllNonHumanAnimalData
                )
            )
            val localCacheRepository = FakeLocalCacheRepository(
                localCacheList = mutableListOf(
                    localCache.copy(
                        cachedObjectId = fosterHome.id,
                        section = Section.FOSTER_HOMES
                    ).toEntity()
                )
            )
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                fireStoreRemoteFosterHomeRepository = fireStoreRemoteFosterHomeRepository,
                localFosterHomeRepository = localFosterHomeRepository,
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
                localCacheRepository = localCacheRepository
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                false,
                {},
                {},
            )

            runCurrent()

            assertTrue {
                fireStoreRemoteFosterHomeRepository.getRemoteFosterHome(fosterHome.id)
                    .firstOrNull() == null
            }
            assertTrue { localFosterHomeRepository.getFosterHome(fosterHome.id) == null }
            assertTrue {
                localCacheRepository.getLocalCacheEntity(
                    fosterHome.id,
                    Section.FOSTER_HOMES
                ) == null
            }
        }

    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the current image from the remote datasource_then the app displays an error`() =
        runTest {
            val fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                remoteFosterHomeList = mutableListOf(fosterHome.toData())
            )
            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                fireStoreRemoteFosterHomeRepository = fireStoreRemoteFosterHomeRepository
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                false,
                {},
                {},
            )

            assertTrue {
                fireStoreRemoteFosterHomeRepository
                    .getRemoteFosterHome(fosterHome.id)
                    .firstOrNull() != null
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my foster home_when I click to delete my foster home but fails deleting the foster home from the local cache_then the deletion process is finished but the local cache is not deleted`() =
        runTest {
            val localFosterHomeRepository = FakeLocalFosterHomeRepository(
                localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                    fosterHomeWithAllNonHumanAnimalData
                )
            )

            val deleteFosterHomeUtil = getDeleteFosterHomeUtil(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
                localFosterHomeRepository = localFosterHomeRepository,
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
                localCacheRepository = FakeLocalCacheRepository()
            )

            deleteFosterHomeUtil.deleteFosterHome(
                fosterHome.id,
                fosterHome.ownerId,
                this,
                true,
                {},
                {},
            )

            runCurrent()

            assertTrue { localFosterHomeRepository.getFosterHome(fosterHome.id) == null }
        }
}
