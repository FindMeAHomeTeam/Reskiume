package com.findmeahometeam.reskiume.ui.unitTestsWithFakes.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyOnlyActivistsInChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckActivistUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeFireStoreRemoteChatRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLocalChatRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.UiRescueEventDetail
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckRescueEventViewmodelTest : CoroutineTestDispatcher() {

    private fun getCheckRescueEventViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            CheckRescueEvent(rescueEvent.id, rescueEvent.creatorId)
        ),
        checkRescueEventUtil: CheckRescueEventUtil = FakeCheckRescueEventUtil(),
        checkActivistUtil: CheckActivistUtil = FakeCheckActivistUtil(),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        localChatRepository: LocalChatRepository = FakeLocalChatRepository(),
        fireStoreRemoteChatRepository: FireStoreRemoteChatRepository = FakeFireStoreRemoteChatRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        ),
        log: Log = FakeLog()
    ): CheckRescueEventViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getChatFromLocalRepository =
            GetChatFromLocalRepository(localChatRepository)

        val getChatFromRemoteRepository =
            GetChatFromRemoteRepository(fireStoreRemoteChatRepository)

        val modifyOnlyActivistsInChatInRemoteRepository =
            ModifyOnlyActivistsInChatInRemoteRepository(fireStoreRemoteChatRepository)

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val insertChatInLocalRepository =
            InsertChatInLocalRepository(
                localChatRepository,
                authRepository,
                log
            )

        return CheckRescueEventViewmodel(
            saveStateHandleProvider,
            checkRescueEventUtil,
            checkActivistUtil,
            observeAuthStateInAuthDataSource,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            getChatFromLocalRepository,
            getChatFromRemoteRepository,
            insertChatInLocalRepository,
            modifyOnlyActivistsInChatInRemoteRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given a rescue event_when I click to check it_then rescue event is retrieved`() =
        runTest {
            getCheckRescueEventViewmodel().rescueEventDetailState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiRescueEventDetail(
                            rescueEvent = rescueEvent,
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user,
                            chatExist = false
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a rescue event_when I click to check it but the rescue event was not found_then an error is shown`() =
        runTest {
            getCheckRescueEventViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckRescueEvent("wrongId", rescueEvent.creatorId)
                )
            ).rescueEventDetailState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a rescue event_when I click to check it but the creator deleted it_then an error is shown`() =
        runTest {
            getCheckRescueEventViewmodel(
                checkActivistUtil = FakeCheckActivistUtil(null)
            ).rescueEventDetailState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a rescue event to check_when I want to talk to the creator_then the app checks if the user is logged in first`() =
        runTest {
            val checkRescueEventViewmodel = getCheckRescueEventViewmodel()

            checkRescueEventViewmodel.rescueEventDetailState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiRescueEventDetail(
                            rescueEvent = rescueEvent,
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user,
                            chatExist = false
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }

            val result = checkRescueEventViewmodel.isLoggedIn()

            assertTrue { result }
        }

    @Test
    fun `given a rescue event to check_when I want to talk to the creator_then the app checks if the user is not the same as the creator`() =
        runTest {
            val checkRescueEventViewmodel = getCheckRescueEventViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckRescueEvent("wrongId", "otherCreatorId")
                ),
                checkRescueEventUtil = FakeCheckRescueEventUtil(
                    rescueEvent.copy(id = "wrongId", creatorId = "otherCreatorId")
                ),
                checkActivistUtil = FakeCheckActivistUtil(user.copy(uid = "otherCreatorId"))
            )

            checkRescueEventViewmodel.rescueEventDetailState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiRescueEventDetail(
                            rescueEvent = rescueEvent.copy(id = "wrongId", creatorId = "otherCreatorId"),
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user,
                            chatExist = false
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }

            val result = checkRescueEventViewmodel.canIStartTheChat()

            assertTrue { result }
        }
}
