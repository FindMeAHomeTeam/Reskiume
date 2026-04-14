package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
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
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteRescueEventUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSubscriptionManagerUtil
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.ModifyRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifyRescueEventViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getModifyRescueEventViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            ModifyRescueEvent(rescueEvent.id)
        ),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = FakeDeleteNonHumanAnimalUtil(),
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
        deleteRescueEventUtil: DeleteRescueEventUtil = FakeDeleteRescueEventUtil(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(
            mutableListOf(userWithAllSubscriptionData)
        ),
        subscriptionManagerUtil: SubscriptionManagerUtil = FakeSubscriptionManagerUtil(),
        log: Log = FakeLog()
    ): ModifyRescueEventViewmodel {

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
            getModifyRescueEventViewmodel(
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                )
            ).rescueEventFlow.test {
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
                assertEquals(
                    listOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given my rescue event to modify_when I add a need to cover and a non human animal to rescue_then I click to update my rescue event`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toData()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    mutableListOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "789"))
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toEntity()
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
        }

    @Test
    fun `given my rescue event to modify_when the app tries to delete the remote image but fails to retrieve the rescue event from the remote repo_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel()

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails deleting the remote rescue event image_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                )
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to modify_when click to update my rescue event but fails retrieving the local rescue event_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    )
                )
            )

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails deleting the local rescue event image_then the app retrieves an error`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
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

            modifyRescueEventViewmodel.saveRescueEventChanges(true, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but there is no rescue event image_then the rescue event is updated`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    remoteRescueEventList = mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    localRescueEventWithAllNeedsAndNonHumanAnimalDataList = mutableListOf(
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toData()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    mutableListOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "789"))
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "789").toEntity()
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
        }

    @Test
    fun `given my rescue event to modify_when I click to update my rescue event but fails retrieving the rescue event from the remote repo_then the rescue event is not updated`() =
        runTest {
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel()

            modifyRescueEventViewmodel.saveRescueEventChanges(false, rescueEvent)

            modifyRescueEventViewmodel.manageChangesUiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
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
            val storageRepository = FakeStorageRepository(
                localDatasourceList = mutableListOf(
                    Pair(
                        "local_path",
                        rescueEvent.imageUrl
                    )
                )
            )
            val modifyRescueEventViewmodel = getModifyRescueEventViewmodel(
                storageRepository = storageRepository
            )
            modifyRescueEventViewmodel.deleteLocalImage(rescueEvent.imageUrl)

            assertTrue { storageRepository.localDatasourceList.isEmpty() }
        }
}
