package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckAllMyRescueEventsUtilIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllMyRescueEventsUtil(
        storageRepository: StorageRepository = FakeStorageRepository(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        ),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity(),
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
            )
        ), manageImagePath: ManageImagePath = FakeManageImagePath(),
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog()
    ): CheckAllMyRescueEventsUtilImpl {

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        val insertRescueEventInLocalRepository =
            InsertRescueEventInLocalRepository(
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                manageImagePath,
                authRepository,
                log
            )

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val modifyRescueEventInLocalRepository =
            ModifyRescueEventInLocalRepository(
                manageImagePath,
                checkNonHumanAnimalUtil,
                localRescueEventRepository,
                localNonHumanAnimalRepository,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        return CheckAllMyRescueEventsUtilImpl(
            downloadImageToLocalDataSource,
            getRescueEventFromLocalRepository,
            insertRescueEventInLocalRepository,
            insertCacheInLocalRepository,
            modifyRescueEventInLocalRepository,
            modifyCacheInLocalRepository,
            log
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a new rescue event list_when the app manage them_then it inserts it in the local repository`() =
        runTest {
            val localRescueEventRepository: LocalRescueEventRepository =
                FakeLocalRescueEventRepository()

            getCheckAllMyRescueEventsUtil(
                localRescueEventRepository = localRescueEventRepository,
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(
                    listOf(rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp")),
                    awaitItem()
                )
                awaitComplete()
            }

            runCurrent()

            assertTrue { localRescueEventRepository.getRescueEvent(rescueEvent.id) != null }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a new rescue event list without avatar_when the app manage them but fails the insertion in the local cache_then it wont be inserted in the local cache`() =
        runTest {
            val localRescueEventRepository: LocalRescueEventRepository =
                FakeLocalRescueEventRepository()
            val localCacheRepository = FakeLocalCacheRepository(
                mutableListOf(
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    ).toEntity()
                )
            )

            getCheckAllMyRescueEventsUtil(
                localRescueEventRepository = localRescueEventRepository,
                localCacheRepository = localCacheRepository,
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    mutableListOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                )
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent.copy(imageUrl = ""))),
                user.uid,
                this
            ).test {
                assertEquals(
                    listOf(rescueEvent.copy(imageUrl = "")),
                    awaitItem()
                )
                awaitComplete()
            }

            runCurrent()

            assertTrue { localRescueEventRepository.getRescueEvent(rescueEvent.id) != null }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an existent rescue event list_when the app manage them_then it modifies it in the local repository`() =
        runTest {
            val localRescueEventRepository: LocalRescueEventRepository =
                FakeLocalRescueEventRepository(
                    mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            rescueEventEntity = rescueEvent.copy(description = "vegan activism")
                                .toEntity()
                        )
                    )
                )

            getCheckAllMyRescueEventsUtil(
                localRescueEventRepository = localRescueEventRepository,
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(
                    listOf(rescueEvent.copy(imageUrl = "${rescueEvent.creatorId}${rescueEvent.id}.webp")),
                    awaitItem()
                )
                awaitComplete()
            }

            runCurrent()

            val expectedRescueEventEntity =
                rescueEvent.copy(imageUrl = "userUid123456userUid123.webp").toEntity()

            assertTrue { localRescueEventRepository.getRescueEvent(rescueEvent.id)!!.rescueEventEntity == expectedRescueEventEntity }
        }
}
