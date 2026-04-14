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
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NonHumanAnimalToRescue
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueNeed
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.ModifyRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.ModifyRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyRescueEventViewmodelTest : CoroutineTestDispatcher() {

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteLocalCacheEntity = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onImageDeletedFromRemoteForRescueEvent =
        Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onImageDeletedFromLocalForRescueEvent = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onUploadImageToRemoteForRescueEvent = Capture.slot<(imagePath: String) -> Unit>()

    private val onModifyRemoteRescueEvent = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyRemoteNonHumanAnimal = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertThirdNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToSaveForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertThirdNonHumanAnimalToSaveForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyRescueEventWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val modifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onCompletedDeleteRescueEvent = Capture.slot<() -> Unit>()

    private val onUnsubscribeRescueEvent = Capture.slot<() -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getModifyRescueEventViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturnForRescueEvent: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        numberOfRowsDeletedInLocalDatasourceArg: Int = 1,
        remoteRescueEventReturn: Flow<RemoteRescueEvent?> = flowOf(rescueEvent.toData()),
        databaseResultOfModifyingRescueEventsInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg: DatabaseResult = DatabaseResult.Success,
        isRemoteImageDeletedFlagForRescueEvent: Boolean = true,
        isLocalImageDeletedFlagForRescueEvent: Boolean = true,
        imagePathToUploadToRemoteForRescueEvent: String = rescueEvent.imageUrl,
        insertedRowIdOfNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfThirdNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfNonHumanAnimalForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNonHumanAnimalForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfThirdNonHumanAnimalForRescueEventInLocalArg: Long = 1L,
        modifiedRescueEventInLocalRowsUpdatedArg: Int = 1,
        modifiedRescueEventWithoutImageInLocalRowsUpdatedArg: Int = 1,
        rescueEventWithAllNeedsAndNonHumanAnimalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
        numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1
    ): ModifyRescueEventViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<ModifyRescueEvent>(any(), any())
            } returns ModifyRescueEvent(rescueEvent.id)
        }

        val authRepository: AuthRepository = mock {
            every { authState } returns (flowOf(authStateReturn))
        }

        val localUserRepository: LocalUserRepository = mock {
            every { getUser(authUser.uid) } returns flowOf(userWithAllSubscriptionData)
        }

        val localCacheRepository: LocalCacheRepository = mock {

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
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }

            everySuspend {
                deleteLocalCacheEntity(
                    rescueEvent.id,
                    capture(onDeleteLocalCacheEntity)
                )
            } calls {
                onDeleteLocalCacheEntity.get().invoke(numberOfRowsDeletedInLocalDatasourceArg)
            }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            every {
                getRemoteRescueEvent(rescueEvent.id)
            } returns remoteRescueEventReturn

            everySuspend {

                modifyRemoteRescueEvent(
                    any(),
                    capture(onModifyRemoteRescueEvent)
                )
            } calls {
                onModifyRemoteRescueEvent.get()
                    .invoke(databaseResultOfModifyingRescueEventsInRemoteRepositoryArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {
                every {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                every {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id + "789", nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(
                            nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                        ).toData(),
                        capture(onModifyRemoteNonHumanAnimal)
                    )
                } calls {
                    onModifyRemoteNonHumanAnimal.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalInRemoteRepositoryArg)
                }

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
            every {
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

            every {
                uploadImage(
                    user.uid,
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    rescueEvent.imageUrl,
                    capture(onUploadImageToRemoteForRescueEvent)
                )
            } calls {
                onUploadImageToRemoteForRescueEvent.get()
                    .invoke(imagePathToUploadToRemoteForRescueEvent)
            }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

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
                insertNeedToCoverEntityForRescueEvent(
                    rescueEvent.allNeedsToCover[0].copy(
                        needToCoverId = "${RescueNeed.RESCUERS.name}123456"
                    ).toEntity(),
                    capture(onInsertThirdNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertThirdNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfThirdNeedToCoverForRescueEventInLocalArg)
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
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].copy(nonHumanAnimalId = nonHumanAnimal.id + "789")
                        .toEntity(),
                    capture(onInsertThirdNonHumanAnimalToSaveForRescueEvent)
                )
            } calls {
                onInsertThirdNonHumanAnimalToSaveForRescueEvent.get()
                    .invoke(insertedRowIdOfThirdNonHumanAnimalForRescueEventInLocalArg)
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
                    .invoke(modifiedRescueEventWithoutImageInLocalRowsUpdatedArg)
            }

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns rescueEventWithAllNeedsAndNonHumanAnimalDataReturn
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
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "789",
                        nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(modifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                modifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val deleteRescueEventUtil: DeleteRescueEventUtil = mock {
            every {
                deleteRescueEvent(
                    id = rescueEvent.id,
                    creatorId = rescueEvent.creatorId,
                    coroutineScope = any(),
                    onError = any(),
                    onComplete = capture(onCompletedDeleteRescueEvent)
                )
            } calls { onCompletedDeleteRescueEvent.get().invoke() }
        }

        val subscriptionManagerUtil: SubscriptionManagerUtil  = mock {

            everySuspend {
                unsubscribeFromTopic(
                    user.copy(email = null),
                    rescueEvent.id,
                    any(),
                    capture(onUnsubscribeRescueEvent)
                )
            } calls { onUnsubscribeRescueEvent.get().invoke() }
        }

        val getRescueEventFromLocalRepository =
            GetRescueEventFromLocalRepository(localRescueEventRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val getRescueEventFromRemoteRepository =
            GetRescueEventFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val modifyRescueEventInRemoteRepository =
            ModifyRescueEventInRemoteRepository(
                authRepository,
                fireStoreRemoteRescueEventRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
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

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        return ModifyRescueEventViewmodel(
            saveStateHandleProvider,
            getRescueEventFromLocalRepository,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            getAllNonHumanAnimalsFromLocalRepository,
            getRescueEventFromRemoteRepository,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            uploadImageToRemoteDataSource,
            modifyRescueEventInRemoteRepository,
            modifyRescueEventInLocalRepository,
            modifyCacheInLocalRepository,
            deleteRescueEventUtil,
            observeAuthStateInAuthDataSource,
            getUserFromLocalDataSource,
            subscriptionManagerUtil,
            log
        )
    }

    @Test
    fun `given my rescue event_when I click to modify it_then rescue event is retrieved`() =
        runTest {
            getModifyRescueEventViewmodel().rescueEventFlow.test {
                assertEquals(
                    UiState.Success(
                        UiRescueEvent(
                            rescueEvent,
                            listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            )
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }


    @Test
    fun `given my rescue event to modify_when I want to add non human animals to rescue_then rescue event list available non human animals`() =
        runTest {
            getModifyRescueEventViewmodel().allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given my rescue event to modify_when I add a need to cover and a non human animal to rescue_then I click to update my rescue event`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel()

            val needToCover = NeedToCover(
                needToCoverId = "${RescueNeed.RESCUERS.name}123456",
                rescueNeed = RescueNeed.RESCUERS,
                rescueEventId = rescueEvent.id
            )

            val nonHumanAnimalToRescue = NonHumanAnimalToRescue(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                rescueEventId = rescueEvent.id
            )

            val updatedRescueEvent = rescueEvent.copy(
                allNeedsToCover = rescueEvent.allNeedsToCover + needToCover,
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue + nonHumanAnimalToRescue
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, updatedRescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyRescueEventViewModel",
                    "modifyCacheForRescueEventInLocalDataSource: ${rescueEvent.id} updated in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when the app tries to delete the remote image but fails to retrieve the rescue event from the remote repo_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                remoteRescueEventReturn = flowOf(null)
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewmodel",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source because the remote rescue event does not exist!"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails deleting the remote rescue event image_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                isRemoteImageDeletedFlagForRescueEvent = false
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "deleteCurrentImageFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when click to update my rescue event but fails retrieving the local rescue event_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                rescueEventWithAllNeedsAndNonHumanAnimalDataReturn = null
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source because the local rescue event does not exist!"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails deleting the local rescue event image_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                isLocalImageDeletedFlagForRescueEvent = false
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "deleteCurrentImageFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but there is no rescue event image_then the rescue event is updated`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel()

            val needToCover = NeedToCover(
                needToCoverId = "${RescueNeed.RESCUERS.name}123456",
                rescueNeed = RescueNeed.RESCUERS,
                rescueEventId = rescueEvent.id
            )

            val nonHumanAnimalToRescue = NonHumanAnimalToRescue(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                rescueEventId = rescueEvent.id
            )

            val updatedRescueEvent = rescueEvent.copy(
                imageUrl = "",
                allNeedsToCover = rescueEvent.allNeedsToCover + needToCover,
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue + nonHumanAnimalToRescue
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, updatedRescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyRescueEventViewModel",
                    "uploadNewImageToRemoteDataSource: the download URI from the rescue event ${updatedRescueEvent.id} is blank"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails retrieving the rescue event from the remote repo_then the rescue event is not updated`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                remoteRescueEventReturn = flowOf(null)
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(false, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewmodel",
                    "saveRescueEventChanges: failed to collect the remote rescue event ${rescueEvent.id} in the remote data source because the it does not exist!"
                )
            }
        }


    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails modifying the rescue event in the remote repo_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                databaseResultOfModifyingRescueEventsInRemoteRepositoryArg = DatabaseResult.Error()
            )

            val needToCover = NeedToCover(
                needToCoverId = "${RescueNeed.RESCUERS.name}123456",
                rescueNeed = RescueNeed.RESCUERS,
                rescueEventId = rescueEvent.id
            )

            val nonHumanAnimalToRescue = NonHumanAnimalToRescue(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                rescueEventId = rescueEvent.id
            )

            val updatedRescueEvent = rescueEvent.copy(
                allNeedsToCover = rescueEvent.allNeedsToCover + needToCover,
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue + nonHumanAnimalToRescue
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(false, updatedRescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "modifyRescueEventInRemoteDataSource: failed to update the rescue event ${updatedRescueEvent.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails modifying the rescue event in the local repo_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                modifiedRescueEventInLocalRowsUpdatedArg = 0
            )

            val needToCover = NeedToCover(
                needToCoverId = "${RescueNeed.RESCUERS.name}123456",
                rescueNeed = RescueNeed.RESCUERS,
                rescueEventId = rescueEvent.id
            )

            val nonHumanAnimalToRescue = NonHumanAnimalToRescue(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                rescueEventId = rescueEvent.id
            )

            val updatedRescueEvent = rescueEvent.copy(
                allNeedsToCover = rescueEvent.allNeedsToCover + needToCover,
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue + nonHumanAnimalToRescue
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(false, updatedRescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "modifyRescueEventInLocalDataSource: failed to update the rescue event ${updatedRescueEvent.id} in the local data source"
                )
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails modifying the rescue event cache_then the rescue event is updated`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                localCacheUpdatedInLocalDatasourceArg = 0
            )

            val needToCover = NeedToCover(
                needToCoverId = "${RescueNeed.RESCUERS.name}123456",
                rescueNeed = RescueNeed.RESCUERS,
                rescueEventId = rescueEvent.id
            )

            val nonHumanAnimalToRescue = NonHumanAnimalToRescue(
                nonHumanAnimalId = nonHumanAnimal.id + "789",
                caregiverId = nonHumanAnimal.caregiverId,
                rescueEventId = rescueEvent.id
            )

            val updatedRescueEvent = rescueEvent.copy(
                allNeedsToCover = rescueEvent.allNeedsToCover + needToCover,
                allNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue + nonHumanAnimalToRescue
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(false, updatedRescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "modifyCacheForRescueEventInLocalDataSource: Error updating ${rescueEvent.id} in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given my rescue event to modify_when I click to delete my rescue event_then the rescue event is deleted and unsubscribed from the user subscriptions`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel()

            modifyRescueEventViewmodel.deleteRescueEvent(rescueEvent.id, rescueEvent.creatorId)

            runCurrent()

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an image to discard_when the user clicks on the delete button_then the image is discarded`() =
        runTest {
            val createAccountViewmodel = getModifyRescueEventViewmodel()
            createAccountViewmodel.deleteLocalImage(rescueEvent.imageUrl)

            verify {
                log.d(
                    "ModifyRescueEventViewModel",
                    "deleteLocalImage: the image ${rescueEvent.imageUrl} was deleted successfully in the local data source"
                )
            }
        }

    @Test
    fun `given an image to discard_when the user clicks on the delete button but the deletion fails_then the image is not discarded`() =
        runTest {
            val createAccountViewmodel = getModifyRescueEventViewmodel(
                isLocalImageDeletedFlagForRescueEvent = false
            )
            createAccountViewmodel.deleteLocalImage(rescueEvent.imageUrl)

            verify {
                log.e(
                    "ModifyRescueEventViewModel",
                    "deleteLocalImage: failed to delete the image ${rescueEvent.imageUrl} in the local data source"
                )
            }
        }
}
