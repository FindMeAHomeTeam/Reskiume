package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.AdoptionState
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
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsUtilImpl
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckAllMyRescueEventsUtilTest : CoroutineTestDispatcher() {

    private val onUploadImageToRemoteForRescueEvent = Capture.slot<(imagePath: String) -> Unit>()

    private val onInsertNeedToCoverForRescueEvent = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNeedToCoverForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertRescueEvent = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertRescueEventWithoutImage = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onDeleteNeedToCoverForRescueEvent = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteSecondNeedToCoverForRescueEvent = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteSecondNonHumanAnimalToRescueForRescueEvent =
        Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onModifyRescueEvent = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyRescueEventWithoutImage = Capture.slot<suspend (rowsUpdated: Int) -> Unit>()

    private val onModifyNonHumanAnimalInLocalRepository = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifySecondNonHumanAnimalInLocalRepository =
        Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifiedLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onSaveImageToLocalForRescueEvent = Capture.slot<(imagePath: String) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckAllMyRescueEventsUtil(
        imagePathToUploadToRemoteForRescueEventArg: String = rescueEvent.imageUrl,
        imagePathToDownloadToRemoteForRescueEvent: String = rescueEvent.imageUrl,
        myRescueEventWithAllNonHumanAnimalLocalDataReturn: RescueEventWithAllNeedsAndNonHumanAnimalData? = rescueEventWithAllNeedsAndNonHumanAnimalData,
        insertedRowIdOfNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfRescueEventInLocalArg: Long = 1L,
        insertedRowIdOfRescueEventWithoutImageInLocalArg: Long = 1L,
        deletedRowIdOfNeedToCoverForRescueEventInLocalArg: Int = 1,
        deletedRowIdOfSecondNeedToCoverForRescueEventInLocalArg: Int = 1,
        deletedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg: Int = 1,
        deletedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg: Int = 1,
        modifiedRowIdsOfRescueEventInLocalArg: Int = 1,
        modifiedRowIdOfRescueEventWithoutImageInLocalArg: Int = 1,
        numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1,
        numberOfSecondNonHumanAnimalsUpdatedInLocalRepositoryArg: Int = 1,
        authStateReturn: AuthUser? = authUser,
        insertedRowIdOfLocalCacheInLocalRepositoryArg: Long = 1L,
        modifiedRowIdsOfLocalCacheInLocalRepositoryArg: Int = 1,
        getLocalCacheEntityForRescueEventReturn: LocalCacheEntity? =
            localCache.copy(
                cachedObjectId = rescueEvent.id,
                section = Section.RESCUE_EVENTS
            ).toEntity(),
    ): CheckAllMyRescueEventsUtilImpl {

        val storageRepository: StorageRepository = mock {

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
                    .invoke(imagePathToUploadToRemoteForRescueEventArg)
            }

            every {
                downloadImage(
                    user.uid,
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    capture(onSaveImageToLocalForRescueEvent)
                )
            } calls {
                onSaveImageToLocalForRescueEvent.get()
                    .invoke(imagePathToDownloadToRemoteForRescueEvent)
            }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            everySuspend {
                getRescueEvent(rescueEvent.id)
            } returns myRescueEventWithAllNonHumanAnimalLocalDataReturn

            everySuspend {
                insertNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[0].copy(
                        rescueEventId = rescueEvent.id
                    ).toEntity(),
                    capture(onInsertNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[1].copy(
                        rescueEventId = rescueEvent.id
                    ).toEntity(),
                    capture(onInsertSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onInsertSecondNeedToCoverForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].copy(
                        rescueEventId = rescueEvent.id
                    ).toEntity(),
                    capture(onInsertNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                insertNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].copy(
                        rescueEventId = rescueEvent.id
                    ).toEntity(),
                    capture(onInsertSecondNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onInsertSecondNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(insertedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id,
                        savedBy = authUser.uid
                    ).toEntity(),
                    capture(onInsertRescueEvent)
                )
            } calls {
                onInsertRescueEvent.get().invoke(insertedRowIdOfRescueEventInLocalArg)
            }

            everySuspend {
                insertRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id,
                        savedBy = authUser.uid,
                        imageUrl = ""
                    ).toEntity(),
                    capture(onInsertRescueEventWithoutImage)
                )
            } calls {
                onInsertRescueEventWithoutImage.get()
                    .invoke(insertedRowIdOfRescueEventWithoutImageInLocalArg)
            }

            everySuspend {
                deleteNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[0].needToCoverId,
                    capture(onDeleteNeedToCoverForRescueEvent)
                )
            } calls {
                onDeleteNeedToCoverForRescueEvent.get()
                    .invoke(deletedRowIdOfNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                deleteNeedToCoverEntityForRecueEvent(
                    rescueEvent.allNeedsToCover[1].needToCoverId,
                    capture(onDeleteSecondNeedToCoverForRescueEvent)
                )
            } calls {
                onDeleteSecondNeedToCoverForRescueEvent.get()
                    .invoke(deletedRowIdOfSecondNeedToCoverForRescueEventInLocalArg)
            }

            everySuspend {
                deleteNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[0].nonHumanAnimalId,
                    capture(onDeleteNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onDeleteNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(deletedRowIdOfNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                deleteNonHumanAnimalToRescueEntityForRescueEvent(
                    rescueEvent.allNonHumanAnimalsToRescue[1].nonHumanAnimalId,
                    capture(onDeleteSecondNonHumanAnimalToRescueForRescueEvent)
                )
            } calls {
                onDeleteSecondNonHumanAnimalToRescueForRescueEvent.get()
                    .invoke(deletedRowIdOfSecondNonHumanAnimalToRescueForRescueEventInLocalArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id,
                        savedBy = authUser.uid
                    ).toEntity(),
                    capture(onModifyRescueEvent)
                )
            } calls {
                onModifyRescueEvent.get().invoke(modifiedRowIdsOfRescueEventInLocalArg)
            }

            everySuspend {
                modifyRescueEvent(
                    rescueEvent.copy(
                        id = rescueEvent.id,
                        savedBy = authUser.uid,
                        imageUrl = ""
                    ).toEntity(),
                    capture(onModifyRescueEventWithoutImage)
                )
            } calls {
                onModifyRescueEventWithoutImage.get()
                    .invoke(modifiedRowIdOfRescueEventWithoutImageInLocalArg)
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

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(onModifyNonHumanAnimalInLocalRepository)
                )
            } calls {
                onModifyNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(
                        id = nonHumanAnimal.id + "second",
                        adoptionState = AdoptionState.NEEDS_TO_BE_RESCUED
                    ).toEntity(),
                    capture(onModifySecondNonHumanAnimalInLocalRepository)
                )
            } calls {
                onModifySecondNonHumanAnimalInLocalRepository.get()
                    .invoke(numberOfSecondNonHumanAnimalsUpdatedInLocalRepositoryArg)
            }
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl) } returns nonHumanAnimal.imageUrl

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(insertedRowIdOfLocalCacheInLocalRepositoryArg)
            }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifiedLocalCacheEntity)
                )
            } calls {
                onModifiedLocalCacheEntity.get()
                    .invoke(modifiedRowIdsOfLocalCacheInLocalRepositoryArg)
            }

            everySuspend {
                getLocalCacheEntity(
                    rescueEvent.id,
                    Section.RESCUE_EVENTS
                )
            } returns getLocalCacheEntityForRescueEventReturn
        }

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
            getCheckAllMyRescueEventsUtil(
                myRescueEventWithAllNonHumanAnimalLocalDataReturn = null
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
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
    fun `given a new rescue event list_when the app manage them but fails the insertion in the local repo_then it wont be inserted`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                myRescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                insertedRowIdOfRescueEventInLocalArg = 0
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "insertRescueEventInLocalRepo: Error adding the rescue event ${rescueEvent.id} to local database"
                )
            }
        }

    @Test
    fun `given a new rescue event list without avatar_when the app manage them but fails the insertion in the local cache_then it wont be inserted`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                imagePathToUploadToRemoteForRescueEventArg = "",
                myRescueEventWithAllNonHumanAnimalLocalDataReturn = null,
                insertedRowIdOfLocalCacheInLocalRepositoryArg = 0
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
    fun `given an existent rescue event list_when the app manage them_then it modifies it in the local repository`() =
        runTest {
            getCheckAllMyRescueEventsUtil().downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
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
    fun `given an existent rescue event list_when the app manage them but fails the modification in the local repo_then it wont be modified`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                modifiedRowIdsOfRescueEventInLocalArg = 0
            ).downloadImageAndManageRescueEventsInLocalRepositoryFromFlow(
                flowOf(listOf(rescueEvent)),
                user.uid,
                this
            ).test {
                assertEquals(listOf(rescueEvent), awaitItem())
                awaitComplete()
            }
            verify {
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Error modifying the rescue event ${rescueEvent.id} in local database"
                )
            }
        }

    @Test
    fun `given an existent rescue event list without avatar_when the app manage them but fails the modification in the local cache_then it wont be inserted`() =
        runTest {
            getCheckAllMyRescueEventsUtil(
                imagePathToUploadToRemoteForRescueEventArg = "",
                modifiedRowIdsOfLocalCacheInLocalRepositoryArg = 0
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
                    "modifyRescueEventInLocalRepo: Rescue event ${rescueEvent.id} modified in local database"
                )
                log.e(
                    "CheckAllMyRescueEventsUtilImpl",
                    "modifyRescueEventInLocalRepo: Error updating ${rescueEvent.id} in local cache in section ${Section.RESCUE_EVENTS}"
                )
            }
        }
}
