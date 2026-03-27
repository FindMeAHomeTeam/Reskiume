package com.findmeahometeam.reskiume.ui.integrationTests.rescueEvents

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckActivistUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckRescueEventViewmodelIntegrationTest : CoroutineTestDispatcher() {

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
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
            mutableListOf(
                nonHumanAnimal,
                nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
            )
        )
    ): CheckRescueEventViewmodel {

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
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckRescueEvent("wrongId", rescueEvent.creatorId)
                )
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
                checkActivistUtil = FakeCheckActivistUtil(null)
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
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckRescueEvent("wrongId", "otherCreatorId")
                ),
                checkRescueEventUtil = FakeCheckRescueEventUtil(
                    rescueEvent.copy(id = "wrongId", creatorId = "otherCreatorId")
                ),
                checkActivistUtil = FakeCheckActivistUtil(user.copy(uid = "otherCreatorId"))
            )

            checkRescueEventViewmodel.rescueEventFlow.test {
                assertEquals(
                    UiState.Success(
                        UiRescueEvent(
                            rescueEvent = rescueEvent.copy(id = "wrongId", creatorId = "otherCreatorId"),
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
