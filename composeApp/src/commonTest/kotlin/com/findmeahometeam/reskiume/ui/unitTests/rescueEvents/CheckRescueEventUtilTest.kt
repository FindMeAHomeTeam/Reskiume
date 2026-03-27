package com.findmeahometeam.reskiume.ui.unitTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteRescueEvent
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
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
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtilImpl
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

class CheckRescueEventUtilTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageSavedToLocalFromRescueEvent = Capture.slot<(imagePath: String) -> Unit>()

    private val onModifyRemoteRescueEvent = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToSaveForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertRescueEventWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyRescueEventWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val modifySecondNonHumanAnimalInLocalRepository =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckRescueEventUtilImpl(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        remoteRescueEventReturn: Flow<RemoteRescueEvent> = flowOf(rescueEvent.toData()),
        insertedRescueEventInLocalRowsUpdatedArg: Long = 1L,
        modifiedRescueEventInLocalRowsUpdatedArg: Int = 1,
        insertedRowIdOfNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfNonHumanAnimalForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNonHumanAnimalForRescueEventInLocalArg: Long = 1L,
        rescueEventWithAllNeedsAndNonHumanAnimalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
    ): CheckRescueEventUtilImpl {

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
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityReturnForRescueEvent

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(1) }

            everySuspend {
                deleteLocalCacheEntity(
                    rescueEvent.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(1)
            }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            everySuspend {
                getRemoteRescueEvent(rescueEvent.id)
            } returns remoteRescueEventReturn

            everySuspend {

                modifyRemoteRescueEvent(
                    rescueEvent.toData(),
                    capture(onModifyRemoteRescueEvent)
                )
            } calls {
                onModifyRemoteRescueEvent.get()
                    .invoke(DatabaseResult.Success)
            }

            everySuspend {

                modifyRemoteRescueEvent(
                    rescueEvent.copy(imageUrl = "").toData(),
                    capture(onModifyRemoteRescueEvent)
                )
            } calls {
                onModifyRemoteRescueEvent.get()
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

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    user.uid,
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    capture(onImageSavedToLocalFromRescueEvent)
                )
            } calls {
                onImageSavedToLocalFromRescueEvent.get()
                    .invoke(rescueEvent.imageUrl)
            }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onInsertRescueEvent)
                )
            } calls {
                onInsertRescueEvent.get().invoke(insertedRescueEventInLocalRowsUpdatedArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onInsertRescueEventWithoutImage)
                )
            } calls {
                onInsertRescueEventWithoutImage.get()
                    .invoke(1)
            }

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].toEntity(),
                    capture(onInsertNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[1].toEntity(),
                    capture(onInsertSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertSecondNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].toEntity(),
                    capture(onInsertNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfNonHumanAnimalForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].toEntity(),
                    capture(onInsertSecondNonHumanAnimalToSaveForRescueEvent)
                )
            } calls {
                onInsertSecondNonHumanAnimalToSaveForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNonHumanAnimalForRescueEventInLocalArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid).toEntity(),
                    capture(onModifyRescueEvent)
                )
            } calls {
                onModifyRescueEvent.get().invoke(modifiedRescueEventInLocalRowsUpdatedArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(savedBy = authUser.uid, imageUrl = "").toEntity(),
                    capture(onModifyRescueEventWithoutImage)
                )
            } calls {
                onModifyRescueEventWithoutImage.get()
                    .invoke(1)
            }

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns rescueEventWithAllNeedsAndNonHumanAnimalDataReturn
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(1)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "second",
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(modifySecondNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifySecondNonHumanAnimalInLocalRepository.get()
                    .invoke(1)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

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
                getLocalCacheEntityReturnForRescueEvent = null
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
            verify {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "insertRescueEventsInLocalRepository: Rescue event ${rescueEvent.id} added to local database"
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a rescue event from remote with empty avatar_then rescue event is retrieved and inserted in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                getLocalCacheEntityReturnForRescueEvent = null,
                remoteRescueEventReturn = flowOf(rescueEvent.copy(imageUrl = "").toData())
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
            verify {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "downloadImageAndInsertRescueEventInLocalRepository: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                )
            }
        }

    @Test
    fun `given an empty cache_when the user request a rescue event from remote but fails inserting it in the local repo_then rescue event is retrieved but not inserted in the local repo`() =
        runTest {
            getCheckRescueEventUtilImpl(
                getLocalCacheEntityReturnForRescueEvent = null,
                insertedRescueEventInLocalRowsUpdatedArg = 0
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
            verify {
                log.e(
                    "CheckRescueEventUtilImpl",
                    "insertRescueEventsInLocalRepository: Error adding the Rescue event ${rescueEvent.id} to local database"
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a rescue event from remote_then rescue event is retrieved and modified in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                getLocalCacheEntityReturnForRescueEvent =
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS,
                        timestamp = 123L
                    ).toEntity()
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
            verify {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "modifyRescueEventsInLocalRepository: Rescue event ${rescueEvent.id} modified in local database"
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a rescue event from remote with empty avatar_then rescue event is retrieved and modified in the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl(
                getLocalCacheEntityReturnForRescueEvent =
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS,
                        timestamp = 123L
                    ).toEntity(),
                remoteRescueEventReturn = flowOf(rescueEvent.copy(imageUrl = "").toData())
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
            verify {
                log.d(
                    "CheckRescueEventUtilImpl",
                    "downloadImageAndModifyRescueEventInLocalRepository: Rescue event ${rescueEvent.id} has no avatar image to save locally."
                )
            }
        }

    @Test
    fun `given an outdated cache_when the user request a rescue event from remote but fails modifying it in the local repo_then rescue event is retrieved but not modified in the local repo`() =
        runTest {
            getCheckRescueEventUtilImpl(
                getLocalCacheEntityReturnForRescueEvent =
                    localCache.copy(
                        cachedObjectId = rescueEvent.id,
                        section = Section.RESCUE_EVENTS,
                        timestamp = 123L
                    ).toEntity(),
                modifiedRescueEventInLocalRowsUpdatedArg = 0
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
            verify {
                log.e(
                    "CheckRescueEventUtilImpl",
                    "modifyRescueEventsInLocalRepository: Error modifying the Rescue event ${rescueEvent.id} in local database"
                )
            }
        }

    @Test
    fun `given recent cache_when the user request a rescue event_then it is retrieved from the local cache`() =
        runTest {
            getCheckRescueEventUtilImpl().getRescueEventFlow(
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
            verify {
                log.d(
                    "GetDataByManagingObjectLocalCacheTimestamp",
                    "Cache for ${rescueEvent.id} in section ${Section.RESCUE_EVENTS} is up-to-date."
                )
            }
        }

    @Test
    fun `given recent cache_when the user request a rescue event but there is an issue retrieving it from the local cache_then it is not retrieved`() =
        runTest {
            getCheckRescueEventUtilImpl(
                rescueEventWithAllNeedsAndNonHumanAnimalDataReturn = null
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
