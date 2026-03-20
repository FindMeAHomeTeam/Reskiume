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
import dev.mokkery.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyRescueEventsUtilIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

    private fun getCheckAllMyRescueEventsUtil(
        storageRepository: StorageRepository = FakeStorageRepository(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository()
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

    @Test
    fun `given a new rescue event list_when the app manage them_then it inserts it in the local repository`() =
        runTest {
            getCheckAllMyRescueEventsUtil().downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
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
            verify {
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: Rescue event ${rescueEvent.id} added to local database"
                )
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: ${rescueEvent.id} added to local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @Test
    fun `given a new rescue event list without avatar_when the app manage them but fails the insertion in the local cache_then it wont be inserted`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                ),
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
                assertEquals(listOf(rescueEvent.copy(imageUrl = "")), awaitItem())
                awaitComplete()
            }
            verify {
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "downloadImageAndManageRescueEventsInLocalRepositoryFromFlow: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                )
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: Error adding ${rescueEvent.id} to local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @Test
    fun `given a rescue event list_when the app manage them but it does exist in cache_then it modifies it in the local repository`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    mutableListOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
                ),
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
            verify {
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Rescue event ${rescueEvent.id} modified in local database"
                )
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: ${rescueEvent.id} updated in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @Test
    fun `given an existent rescue event list_when the app manage them_then it modifies it in the local repository`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    mutableListOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                ),
            ).downloadImageAndModifyRescueEventsInLocalRepositoryFromFlow(
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
            verify {
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Rescue event ${rescueEvent.id} modified in local database"
                )
                log.d(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: ${rescueEvent.id} updated in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
}
