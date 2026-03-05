package com.findmeahometeam.reskiume.ui.integrationTests.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.model.AdoptionState
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
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckActivistUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckReviewsUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.uiReview
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckFosterHomeViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckFosterHomeViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(
            CheckFosterHome(fosterHome.id, fosterHome.ownerId)
        ),
        checkFosterHomeUtil: CheckFosterHomeUtil = FakeCheckFosterHomeUtil(),
        checkActivistUtil: CheckActivistUtil = FakeCheckActivistUtil(),
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        checkReviewsUtil: CheckReviewsUtil = FakeCheckReviewsUtil(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath()
    ): CheckFosterHomeViewmodel {

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
                            uiAllResidentNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user
                        )
                    ),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home_when I click to check it but the owner deleted it_then an error is shown`() =
        runTest {
            getCheckFosterHomeViewmodel(
                checkActivistUtil = FakeCheckActivistUtil(null),
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
            getCheckFosterHomeViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
            ).allAvailableNonHumanAnimalsLookingForAdoptionFlow.test {
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner about a non human animal but I do not have any available_then the foster home do not list them`() =
        runTest {
            getCheckFosterHomeViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.copy(adoptionState = AdoptionState.REHOMED).toEntity())
                )
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
    fun `given a foster home to check_when I want to talk to the owner and I am logged in_then the foster home check the available non human animals`() =
        runTest {
            val checkFosterHomeViewmodel = getCheckFosterHomeViewmodel()

            checkFosterHomeViewmodel.fosterHomeFlow.test {
                assertEquals(
                    UiState.Success(
                        UiFosterHome(
                            fosterHome = fosterHome,
                            uiAllResidentNonHumanAnimals = listOf(nonHumanAnimal),
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
}
