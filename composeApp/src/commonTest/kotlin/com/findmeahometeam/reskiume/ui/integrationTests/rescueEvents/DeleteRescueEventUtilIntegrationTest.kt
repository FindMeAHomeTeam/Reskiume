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
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteRescueEventUtilIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

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
        )
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

    @Test
    fun `given my rescue event_when I click to delete my rescue event_then the rescue event is deleted`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
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
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = rescueEvent.id,
                            section = Section.RESCUE_EVENTS
                        ).toEntity()
                    )
                )
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            verify {
                log.d(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Rescue event ${rescueEvent.id} deleted in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the current image from the remote datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                )
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails retrieving the local rescue event trying to delete its local image_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    )
                )
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source because the local rescue event does not exist"
                )
            }
        }

    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the current image from the local datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    )
                )
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source"
                )
            }
        }

    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the rescue event from the local cache_then the deletion process is finished but the local cache is not deleted`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
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
                )
            )

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                true,
                {},
                {},
            )

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Error deleting the rescue event ${rescueEvent.id} in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
}
