package com.findmeahometeam.reskiume.ui.unitTests.rescueEvents

import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
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
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtilImpl
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

class DeleteRescueEventUtilTest : CoroutineTestDispatcher() {

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageDeletedFromRemoteForRescueEvent =
        Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onImageDeletedFromLocalForRescueEvent = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onModifyLocalNonHumanAnimal = Capture.slot<(rowsModified: Int) -> Unit>()

    private val onModifySecondLocalNonHumanAnimal = Capture.slot<(rowsModified: Int) -> Unit>()

    private val onDeleteRemoteRescueEvent = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifySecondRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteRescueEventFromLocalRepository =
        Capture.slot<suspend (rowsDeleted: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getDeleteRescueEventUtil(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        numberOfRowsDeletedInLocalCacheArg: Int = 1,
        remoteRescueEventReturn: Flow<RemoteRescueEvent?> = flowOf(rescueEvent.toData()),
        databaseResultOfDeletingRescueEventsInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingSecondNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        isRemoteImageDeletedFlagForRescueEvent: Boolean = true,
        isLocalImageDeletedFlagForRescueEvent: Boolean = true,
        rescueEventWithAllNeedsAndNonHumanAnimalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
        modifiedNonHumanAnimalsInLocalRepositoryArg: Int = 1,
        modifiedSecondNonHumanAnimalsInLocalRepositoryArg: Int = 1,
        rowsOfRescueEventsDeletedFromLocalRepositoryArg: Int = 1
    ): DeleteRescueEventUtilImpl {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForRescueEvent

            everySuspend {
                deleteLocalCacheEntity(
                    rescueEvent.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(numberOfRowsDeletedInLocalCacheArg)
            }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            everySuspend {
                getRemoteRescueEvent(rescueEvent.id)
            } returns remoteRescueEventReturn

            everySuspend {
                deleteRemoteRescueEvent(
                    rescueEvent.id,
                    capture(onDeleteRemoteRescueEvent)
                )
            } calls {
                onDeleteRemoteRescueEvent.get()
                    .invoke(databaseResultOfDeletingRescueEventsInRemoteRepositoryArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                everySuspend {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    getRemoteNonHumanAnimal(
                        nonHumanAnimal.id + "second",
                        nonHumanAnimal.caregiverId
                    )
                } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData())

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

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                        capture(onModifySecondRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifySecondRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingSecondNonHumanAnimalInRemoteRepositoryArg)
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
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))

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
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    capture(onImageDeletedFromRemoteForRescueEvent)
                )
            } calls {
                onImageDeletedFromRemoteForRescueEvent.get()
                    .invoke(isRemoteImageDeletedFlagForRescueEvent)
            }

            every {
                deleteLocalImage(
                    rescueEvent.imageUrl,
                    capture(onImageDeletedFromLocalForRescueEvent)
                )
            } calls {
                onImageDeletedFromLocalForRescueEvent.get()
                    .invoke(isLocalImageDeletedFlagForRescueEvent)
            }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns rescueEventWithAllNeedsAndNonHumanAnimalDataReturn

            everySuspend {
                deleteRescueEvent(
                    rescueEvent.id,
                    capture(onDeleteRescueEventFromLocalRepository)
                )
            } calls {
                onDeleteRescueEventFromLocalRepository.get()
                    .invoke(rowsOfRescueEventsDeletedFromLocalRepositoryArg)
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
                onModifyLocalNonHumanAnimal.get()
                    .invoke(modifiedNonHumanAnimalsInLocalRepositoryArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity(),
                    capture(onModifySecondLocalNonHumanAnimal)
                )
            } calls {
                onModifySecondLocalNonHumanAnimal.get()
                    .invoke(modifiedSecondNonHumanAnimalsInLocalRepositoryArg)
            }
        }

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
            val deleteRescueEventUtil = getDeleteRescueEventUtil()

            deleteRescueEventUtil.deleteRescueEvent(
                rescueEvent.id,
                rescueEvent.creatorId,
                this,
                false,
                {},
                {},
            )

            runCurrent()

            verify {
                log.d(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Rescue event ${rescueEvent.id} deleted in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails retrieving the remote rescue event_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                remoteRescueEventReturn = flowOf(null)
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source because the remote rescue event does not exist!"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the current image from the remote datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                isRemoteImageDeletedFlagForRescueEvent = false
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails retrieving the local rescue event trying to delete its local image_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                rescueEventWithAllNeedsAndNonHumanAnimalDataReturn = null
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source because the local rescue event does not exist"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the current image from the local datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                isLocalImageDeletedFlagForRescueEvent = false
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the rescue event from the remote datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                databaseResultOfDeletingRescueEventsInRemoteRepositoryArg = DatabaseResult.Error()
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventFromRemoteDataSource: Error deleting the rescue event ${rescueEvent.id} in the remote data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the rescue event from the local datasource_then the app displays an error`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                rowsOfRescueEventsDeletedFromLocalRepositoryArg = 0
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventFromLocalDataSource: Error deleting the rescue event ${rescueEvent.id} in the local data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event_when I click to delete my rescue event but fails deleting the rescue event from the local cache_then the deletion process is finished but the local cache is not deleted`() =
        runTest {
            val deleteRescueEventUtil = getDeleteRescueEventUtil(
                numberOfRowsDeletedInLocalCacheArg = 0
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

            verify {
                log.e(
                    "DeleteRescueEventUtil",
                    "deleteRescueEventCacheFromLocalDataSource: Error deleting the rescue event ${rescueEvent.id} in the local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
}
