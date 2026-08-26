package com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetNonHumanAnimalInfoInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.IsFosterHomeInChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.UiFosterHomeDetail
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckActivistUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeCheckReviewsUtil
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeFireStoreRemoteChatRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLocalChatRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes.FakeSaveStateHandleProvider
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

class CheckFosterHomeViewmodelTest : CoroutineTestDispatcher() {

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
        localChatRepository: LocalChatRepository = FakeLocalChatRepository(),
        fireStoreRemoteChatRepository: FireStoreRemoteChatRepository = FakeFireStoreRemoteChatRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog()
    ): CheckFosterHomeViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val getNonHumanAnimalInfoInLocalRepository =
            GetNonHumanAnimalInfoInLocalRepository(localChatRepository)

        val getFosterHomeFromRemoteRepository =
            GetFosterHomeFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val getChatFromLocalRepository =
            GetChatFromLocalRepository(localChatRepository)

        val isFosterHomeInChatInLocalRepository =
            IsFosterHomeInChatInLocalRepository(localChatRepository)

        val insertChatInRemoteRepository =
            InsertChatInRemoteRepository(fireStoreRemoteChatRepository)

        val insertChatInLocalRepository =
            InsertChatInLocalRepository(
                localChatRepository,
                authRepository,
                log
            )

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        return CheckFosterHomeViewmodel(
            saveStateHandleProvider,
            checkFosterHomeUtil,
            checkActivistUtil,
            observeAuthStateInAuthDataSource,
            getImagePathForFileNameFromLocalDataSource,
            checkNonHumanAnimalUtil,
            checkReviewsUtil,
            getAllNonHumanAnimalsFromLocalRepository,
            getNonHumanAnimalInfoInLocalRepository,
            getFosterHomeFromRemoteRepository,
            getChatFromLocalRepository,
            isFosterHomeInChatInLocalRepository,
            insertChatInRemoteRepository,
            insertChatInLocalRepository,
            insertCacheInLocalRepository,
            log
        )
    }

    @Test
    fun `given a foster home_when I click to check it_then foster home is retrieved`() =
        runTest {
            getCheckFosterHomeViewmodel().fosterHomeState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiFosterHomeDetail(
                            fosterHome = fosterHome,
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user,
                            chatExist = false
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a foster home_when I click to check it but the foster home was not found_then an error is shown`() =
        runTest {
            getCheckFosterHomeViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckFosterHome("wrongId", fosterHome.ownerId)
                )
            ).fosterHomeState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a foster home_when I click to check it but the owner deleted it_then an error is shown`() =
        runTest {
            getCheckFosterHomeViewmodel(
                checkActivistUtil = FakeCheckActivistUtil(null)
            ).fosterHomeState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Error(),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner about a non human animal_then the foster home list available non human animals`() =
        runTest {
            getCheckFosterHomeViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
            ).allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.test {
                assertEquals(emptyList(), awaitItem())
                assertEquals(listOf(nonHumanAnimal), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner about a non human animal but I do not have any available_then the foster home do not list them`() =
        runTest {
            getCheckFosterHomeViewmodel(
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.copy(nonHumanAnimalState = NonHumanAnimalState.REHOMED)
                            .toEntity()
                    )
                )
            ).allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow.test {
                assertEquals(emptyList(), awaitItem())
                ensureAllEventsConsumed()
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

            checkFosterHomeViewmodel.fosterHomeState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiFosterHomeDetail(
                            fosterHome = fosterHome,
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user,
                            chatExist = false
                        )
                    ),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }

            val result = checkFosterHomeViewmodel.isLoggedIn()

            assertTrue { result }
        }

    @Test
    fun `given a foster home to check_when I want to talk to the owner_then the app checks if the user is not the same as the owner`() =
        runTest {
            val checkRescueEventViewmodel = getCheckFosterHomeViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(
                    CheckFosterHome("wrongId", "otherOwnerId")
                ),
                checkFosterHomeUtil = FakeCheckFosterHomeUtil(
                    fosterHome.copy(id = "wrongId", ownerId = "otherOwnerId")
                ),
                checkActivistUtil = FakeCheckActivistUtil(user.copy(uid = "otherOwnerId"))
            )

            checkRescueEventViewmodel.fosterHomeState.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertEquals(
                    UiState.Success(
                        UiFosterHomeDetail(
                            fosterHome = fosterHome.copy(id = "wrongId", ownerId = "otherOwnerId"),
                            allResidentUiNonHumanAnimals = listOf(nonHumanAnimal),
                            owner = user,
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
