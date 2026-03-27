package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtilImpl
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckRescueEventUtilIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckRescueEventUtilImpl(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        konnectivity: Konnectivity = FakeKonnectivity(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        ),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity(),
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
            )
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        log: Log = FakeLog()
    ): CheckRescueEventUtilImpl {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(
                localCacheRepository,
                log,
                konnectivity
            )

        val getRescueEventFromRemoteRepository =
            GetRescueEventFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val deleteCacheFromLocalRepository =
            DeleteCacheFromLocalRepository(localCacheRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertRescueEventInLocalRepository =
            InsertRescueEventInLocalRepository(
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                manageImagePath,
                authRepository,
                log
            )

        val modifyRescueEventInLocalRepository =
            ModifyRescueEventInLocalRepository(
                manageImagePath,
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                authRepository,
                log
            )

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        return CheckRescueEventUtilImpl(
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getRescueEventFromRemoteRepository,
            deleteCacheFromLocalRepository,
            downloadImageToLocalDataSource,
            insertRescueEventInLocalRepository,
            modifyRescueEventInLocalRepository,
            getRescueEventFromLocalRepository,
            log
        )
    }

    @Test
    fun `given an empty cache_when the user request a rescue event from remote_then rescue event is retrieved and inserted in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an empty cache_when the user request a rescue event from remote with empty avatar_then rescue event is retrieved and inserted in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.copy(imageUrl = "").toData())
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent.copy(imageUrl = ""),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an empty cache_when the user request a rescue event from remote but fails inserting it in the local repo_then rescue event is retrieved but not inserted in the local repo`() =
        runTest {
            getCheckRescueEventUtilImpl(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an outdated cache_when the user request a rescue event from remote_then rescue event is retrieved and modified in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an outdated cache_when the user request a rescue event from remote with empty avatar_then rescue event is retrieved and modified in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.copy(imageUrl = "").toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            rescueEventEntity = rescueEvent.copy(
                                imageUrl = ""
                            ).toEntity()
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent.copy(imageUrl = ""),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given recent cache_when the user request a rescue event_then it is retrieved from the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
                this
            ).test {
                assertEquals(
                    rescueEvent,
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given recent cache_when the user request a rescue event but there is an issue retrieving it from the local cache_then it is not retrieved`() =
        runTest {
            getCheckRescueEventUtilImpl(
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            ).getRescueEventFlow(
                rescueEvent.id,
                rescueEvent.creatorId,
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
