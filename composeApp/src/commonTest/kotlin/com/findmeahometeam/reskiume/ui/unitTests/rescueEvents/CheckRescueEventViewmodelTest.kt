package com.findmeahometeam.reskiume.ui.unitTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckRescueEventViewmodelTest : CoroutineTestDispatcher() {

    private fun getCheckRescueEventViewmodel(
        rescueEventId: String = rescueEvent.id,
        creatorId: String = rescueEvent.creatorId,
        userReturned: User? = user
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

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        return CheckRescueEventViewmodel(
            saveStateHandleProvider,
            checkRescueEventUtil,
            checkActivistUtil,
            observeAuthStateInAuthDataSource,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil
        )
    }

    @Test
    fun `given a rescue event_when I click to check it_then rescue event is retrieved`() =
        runTest {
            getCheckRescueEventViewmodel().rescueEventFlow.test {
                assertEquals(
                    UiState.Success(
                        UiRescueEvent(
                            rescueEvent = rescueEvent,
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a rescue event_when I click to check it but the rescue event was not found_then an error is shown`() =
        runTest {
            getCheckRescueEventViewmodel(
                rescueEventId = "wrongId"
            ).rescueEventFlow.test {
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a rescue event_when I click to check it but the creator deleted it_then an error is shown`() =
        runTest {
            getCheckRescueEventViewmodel(
                userReturned = null
            ).rescueEventFlow.test {
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a rescue event to check_when I want to talk to the creator_then the app checks if the user is logged in first`() =
        runTest {
            val checkRescueEventViewmodel = getCheckRescueEventViewmodel()

            checkRescueEventViewmodel.rescueEventFlow.test {
                assertEquals(
                    UiState.Success(
                        UiRescueEvent(
                            rescueEvent = rescueEvent,
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
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

            checkRescueEventViewmodel.rescueEventFlow.test {
                assertEquals(
                    UiState.Success(
                        UiRescueEvent(
                            rescueEvent = rescueEvent,
                            allUiNonHumanAnimalsToRescue = listOf(
                                nonHumanAnimal,
                                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                            ),
                            creator = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }

            val result = checkRescueEventViewmodel.canIStartTheChat()

            assertTrue { result }
        }
}
