package com.findmeahometeam.reskiume.ui.unitTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.uiReview
import com.findmeahometeam.reskiume.user
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckFosterHomeViewmodelTest : CoroutineTestDispatcher() {

    private fun getCheckFosterHomeViewmodel(
        fosterHomeId: String = fosterHome.id,
        ownerId: String = fosterHome.ownerId,
        userReturned: User? = user,
        allNonHumanAnimalsReturned: Flow<List<NonHumanAnimalEntity>> = flowOf(listOf(nonHumanAnimal.toEntity()))
    ): CheckFosterHomeViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckFosterHome>(any(), any())
            } returns CheckFosterHome(fosterHomeId, ownerId)
        }

        val checkFosterHomeUtil: CheckFosterHomeUtil = mock {

            every {
                getFosterHomeFlow(fosterHome.id, fosterHome.ownerId, any())
            } returns flowOf(fosterHome)

            every {
                getFosterHomeFlow("otherId", "otherOwnerId", any())
            } returns flowOf(fosterHome)

            every {
                getFosterHomeFlow("wrongId", fosterHome.ownerId, any())
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
        }

        val checkReviewsUtil: CheckReviewsUtil = mock {

            every {
                getReviewListFlow(user.uid)
            } returns flowOf(listOf(uiReview))

            every {
                getReviewListFlow("otherOwnerId")
            } returns flowOf(listOf(uiReview))
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns allNonHumanAnimalsReturned
        }

        val manageImagePath: ManageImagePath = mock {

            every { getImagePathForFileName(fosterHome.imageUrl) } returns fosterHome.imageUrl

            every { getFileNameFromLocalImagePath(fosterHome.imageUrl) } returns fosterHome.imageUrl
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        return CheckFosterHomeViewmodel(
            saveStateHandleProvider,
            checkFosterHomeUtil,
            checkActivistUtil,
            observeAuthStateInAuthDataSource,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            checkReviewsUtil,
            getAllNonHumanAnimalsFromLocalRepository
        )
    }

    @Test
    fun `given a foster home_when I click to check it_then foster home is retrieved`() =
        runTest {
            getCheckFosterHomeViewmodel().fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome = fosterHome,
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home_when I click to check it but the foster home was not found_then an error is shown`() =
        runTest {
            getCheckFosterHomeViewmodel(
                fosterHomeId = "wrongId"
            ).fosterHomeFlow.test {
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home_when I click to check it but the owner deleted it_then an error is shown`() =
        runTest {
            getCheckFosterHomeViewmodel(
                userReturned = null
            ).fosterHomeFlow.test {
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner about a non human animal_then the foster home list available non human animals`() =
        runTest {
            getCheckFosterHomeViewmodel().allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner about a non human animal but I do not have any available_then the foster home do not list them`() =
        runTest {
            getCheckFosterHomeViewmodel(
                allNonHumanAnimalsReturned = flowOf(emptyList())
            ).allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(emptyList(), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home to check_when I want to see the foster home reviews if available_then the foster home list them`() =
        runTest {
            getCheckFosterHomeViewmodel().reviewListFlowState.test {
                assertEquals(UiState.Success(listOf(uiReview)), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner_then the app checks if the user is logged in first`() =
        runTest {
            val checkFosterHomeViewmodel = getCheckFosterHomeViewmodel()

            checkFosterHomeViewmodel.fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome = fosterHome,
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }

            val result = checkFosterHomeViewmodel.isLoggedIn()

            assertTrue { result }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner_then the app checks if the user is not the same as the owner`() =
        runTest {
            val checkFosterHomeViewmodel = getCheckFosterHomeViewmodel(
                fosterHomeId = "otherId",
                ownerId = "otherOwnerId"
            )

            checkFosterHomeViewmodel.fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome = fosterHome,
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }

            val result = checkFosterHomeViewmodel.canIStartTheChat()

            assertTrue { result }
        }
}
