package com.findmeahometeam.reskiume.ui.unitTestsWithMocks.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
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
import com.findmeahometeam.reskiume.rescueEventChat
import com.findmeahometeam.reskiume.rescueEventChatEntityWithAllData
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.UiRescueEventDetail
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckRescueEventViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckRescueEventViewmodel(
        rescueEventId: String = rescueEvent.id,
        creatorId: String = rescueEvent.creatorId,
        userReturned: User? = user,
        localChatReturned: Flow<ChatEntityWithAllData> = flowOf(rescueEventChatEntityWithAllData),
        remoteChatReturned: Flow<RemoteChat> = flowOf(rescueEventChat.toData()),
        databaseResultOfModifyingOnlyActivistsInRemoteChat: Flow<DatabaseResult> = flowOf(DatabaseResult.Success),
        rowIdInsertedInLocalCacheRepository: Long = 1L
    ): CheckRescueEventViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckRescueEvent>(any(), any())
            } returns CheckRescueEvent(rescueEventId, creatorId)
        }

        val checkRescueEventUtil: CheckRescueEventUtil = mock {

            every {
                getRescueEventFlow(rescueEvent.id, rescueEvent.creatorId, any())
            } returns flowOf(rescueEvent)

            every {
                getRescueEventFlow("otherId", "otherCreatorId", any())
            } returns flowOf(rescueEvent)

            every {
                getRescueEventFlow("wrongId", rescueEvent.creatorId, any())
            } returns flowOf(null)
        }

        val checkActivistUtil: CheckActivistUtil = mock {

            everySuspend {
                getUser(user.uid, user.uid)
            } returns userReturned
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authUser))
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

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(rescueEvent.imageUrl) } returns rescueEvent.imageUrl

            every { getFileNameFromLocalImagePath(rescueEvent.imageUrl) } returns rescueEvent.imageUrl
        }

        val localChatRepository: LocalChatRepository = mock {
            every { getChat(rescueEventChat.id) } returns localChatReturned
            every { getChat("otherIdotherCreatorId") } returns localChatReturned
        }

        val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository = mock {
            everySuspend {
                getRemoteChat(rescueEventChat.id)
            } returns remoteChatReturned

            everySuspend {
                modifyOnlyActivistsInRemoteChat(
                    rescueEventChat.id,
                    user.uid,
                    true
                )
            } returns databaseResultOfModifyingOnlyActivistsInRemoteChat
        }

        val localCacheRepository: LocalCacheRepository = mock {
            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(rowIdInsertedInLocalCacheRepository)
            }
        }

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
                            chatExist = true
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
                rescueEventId = "wrongId"
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
                userReturned = null
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
                            chatExist = true
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
                rescueEventId = "otherId",
                creatorId = "otherCreatorId"
            )

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
                            chatExist = true
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
