package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtilImpl
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteRescueEventUtilIntegrationTest : CoroutineTestDispatcher() {

    private fun getDeleteRescueEventUtil(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
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
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
            mutableListOf(
                nonHumanAnimal.toEntity(),
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
            )
        ),
        log: Log = FakeLog()
    ): DeleteRescueEventUtilImpl {

        val getRescueEventFromRemoteRepository =
            GetRescueEventFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val deleteMyRescueEventFromRemoteRepository =
            DeleteMyRescueEventFromRemoteRepository(
                authRepository,
                fireStoreRemoteRescueEventRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

        val deleteMyRescueEventFromLocalRepository =
            DeleteMyRescueEventFromLocalRepository(
                localRescueEventRepository,
                checkNonHumanAnimalUtil,
                localNonHumanAnimalRepository,
                log
            )

        val deleteCacheFromLocalRepository =
            DeleteCacheFromLocalRepository(localCacheRepository)

        return DeleteRescueEventUtilImpl(
            getRescueEventFromRemoteRepository,
            getRescueEventFromLocalRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            deleteMyRescueEventFromRemoteRepository,
            deleteMyRescueEventFromLocalRepository,
            deleteCacheFromLocalRepository,
            log
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event_then the rescue event is deleted`() =
        runTest {
            val fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                remoteRescueEventList = mutableListOf(rescueEvent.toData())
            )
            val localRescueEventRepository = FakeLocalRescueEventRepository(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                    rescueEventWithAllNeedsAndNonHumanAnimalData
                )
            )
            val localCacheRepository = FakeLocalCacheRepository(
                localCacheList = mutableListOf(
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS
                    ).toEntity()
                )
            )
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = fireStoreRemoteRescueEventRepository,
                localRescueEventRepository = localRescueEventRepository,
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            rescueEvent.imageUrl
                        )
                    )
                ),
                localCacheRepository = localCacheRepository
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            runCurrent()

            assertTrue {
                fireStoreRemoteRescueEventRepository.getRemoteRescueEvent(rescueEvent.id)
                    .firstOrNull() == null
            }
            assertTrue { localRescueEventRepository.getRescueEvent(rescueEvent.id) == null }
            assertTrue {
                localCacheRepository.getLocalCacheEntity(
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                ) == null
            }
        }

    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the current image from the remote datasource_then the app displays an error`() =
        runTest {
            val fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                remoteRescueEventList = mutableListOf(rescueEvent.toData())
            )
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = fireStoreRemoteRescueEventRepository
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            assertTrue {
                fireStoreRemoteRescueEventRepository
                    .getRemoteRescueEvent(rescueEvent.id)
                    .firstOrNull() != null
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the rescue event from the local cache_then the deletion process is finished but the local cache is not deleted`() =
        runTest {
            val localRescueEventRepository = FakeLocalRescueEventRepository(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                    rescueEventWithAllNeedsAndNonHumanAnimalData
                )
            )

            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
                localRescueEventRepository = localRescueEventRepository,
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            rescueEvent.imageUrl
                        )
                    )
                ),
                localCacheRepository = FakeLocalCacheRepository()
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                true,
                {},
                {},
            )

            runCurrent()

            assertTrue { localRescueEventRepository.getRescueEvent(rescueEvent.id) == null }
        }
}
