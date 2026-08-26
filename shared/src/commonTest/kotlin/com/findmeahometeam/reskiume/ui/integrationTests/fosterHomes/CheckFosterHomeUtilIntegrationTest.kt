package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtilImpl
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckFosterHomeUtilIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckFosterHomeUtilImpl(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        konnectivity: Konnectivity = FakeKonnectivity(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity()
            )
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        log: Log = FakeLog()
    ): CheckFosterHomeUtilImpl {

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
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                )
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home from remote with empty avatar_then foster home is retrieved and inserted in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.copy(imageUrl = "").toData())
                )
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
        }

    @Test
    fun `given an empty cache_when the user request a foster home from remote but fails inserting it in the local repo_then foster home is retrieved but not inserted in the local repo`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                )
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an empty cache_when the user request a foster home but there is an issue retrieving it from the remote repo_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl().getFosterHomeFlow(
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

    @Test
    fun `given an outdated cache_when the user request a foster home from remote_then foster home is retrieved and modified in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).getFosterHomeFlow(
                fosterHome.id,
                fosterHome.ownerId,
                this
            ).test {
                assertEquals(
                    fosterHome.copy(imageUrl = "${fosterHome.ownerId}${fosterHome.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an outdated cache_when the user request a foster home from remote with empty avatar_then foster home is retrieved and modified in the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    remoteFosterHomeList = mutableListOf(fosterHome.copy(imageUrl = "").toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData.copy(
                            fosterHomeEntity = fosterHome.copy(
                                imageUrl = ""
                            ).toEntity()
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
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
        }

    @Test
    fun `given an outdated cache_when the user request a foster home but there is an issue retrieving it from the remote repo_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
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

    @Test
    fun `given recent cache_when the user request a foster home_then it is retrieved from the local cache`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    localFosterHomeWithAllNonHumanAnimalDataList = mutableListOf(
                        fosterHomeWithAllNonHumanAnimalData
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
        }

    @Test
    fun `given recent cache_when the user request a foster home but there is an issue retrieving it from the local cache_then it is not retrieved`() =
        runTest {
            getCheckFosterHomeUtilImpl(
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = fosterHome.id,
                            section = Section.FOSTER_HOMES
                        ).toEntity()
                    )
                )
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
