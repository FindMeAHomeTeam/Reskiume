package com.findmeahometeam.reskiume.ui.unitTestsWithMocks.fosterHomes

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
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
import com.findmeahometeam.reskiume.fosterHomeChat
import com.findmeahometeam.reskiume.fosterHomeChatEntityWithAllData
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEventChat
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeViewmodel
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.UiFosterHomeDetail
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.uiReview
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

class CheckFosterHomeViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertChat = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertNonHumanInfo = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertActivistInfo = Capture.slot<suspend (rowId: Long) -> Unit>()

    private val onInsertLocalCache = Capture.slot<(rowId: Long) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckFosterHomeViewmodel(
        fosterHomeId: String = fosterHome.id,
        ownerId: String = fosterHome.ownerId,
        userReturned: User? = user,
        allNonHumanAnimalsReturned: Flow<List<NonHumanAnimalEntity>> = flowOf(listOf(nonHumanAnimal.toEntity())),
        nonHumanAnimalInfoEntityReturned: Flow<NonHumanAnimalInfoEntity?> = flowOf(
            fosterHomeChat.allNonHumanAnimalsInfo.first().toEntity()
        ),
        chatIdInsertedInLocalRepositoryArg: Long = 1L,
        chatReturned: Flow<ChatEntityWithAllData> = flowOf(fosterHomeChatEntityWithAllData),
        isFosterHomeInChat: Boolean = false,
        nonHumanAnimalInfoIdInsertedInLocalRepositoryArg: Long = 1L,
        activistInfoIdInsertedInLocalRepositoryArg: Long = 1L,
        databaseResultOfInsertingChatInRemoteRepo: Flow<DatabaseResult> = flowOf(DatabaseResult.Success),
        rowIdOfInsertingLocalCacheInLocalRepository: Long = 1L
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

        val localChatRepository: LocalChatRepository = mock {

            every {
                getNonHumanAnimalInfo(nonHumanAnimal.id)
            } returns nonHumanAnimalInfoEntityReturned

            every {
                getChat(fosterHomeChat.id)
            } returns chatReturned

            every {
                getChat("otherIdotherOwnerId")
            } returns chatReturned

            everySuspend {
                isFosterHomeChat(fosterHome.id)
            } returns isFosterHomeInChat

            everySuspend {
                isFosterHomeChat("otherId")
            } returns isFosterHomeInChat

            everySuspend {
                insertChat(
                    rescueEventChat.toEntity(),
                    capture(onInsertChat)
                )
            } calls {
                onInsertChat.get().invoke(chatIdInsertedInLocalRepositoryArg)
            }

            everySuspend {
                insertNonHumanAnimalInfoEntity(
                    rescueEventChat.allNonHumanAnimalsInfo.first().toEntity(),
                    capture(onInsertNonHumanInfo)
                )
            } calls {
                onInsertNonHumanInfo.get().invoke(nonHumanAnimalInfoIdInsertedInLocalRepositoryArg)
            }

            everySuspend {
                insertActivistInfoEntity(
                    rescueEventChat.allActivistsInfo.first().toEntity(),
                    capture(onInsertActivistInfo)
                )
            } calls {
                onInsertActivistInfo.get().invoke(activistInfoIdInsertedInLocalRepositoryArg)
            }
        }

        val fireStoreRemoteChatRepository: FireStoreRemoteChatRepository = mock {

            everySuspend {
                insertRemoteChat(any())
            } returns databaseResultOfInsertingChatInRemoteRepo
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCache)
                )
            } calls {
                onInsertLocalCache.get().invoke(rowIdOfInsertingLocalCacheInLocalRepository)
            }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getRemoteFosterHome(fosterHome.id)
            } returns flowOf(fosterHome.toData())
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
                fosterHomeId = "wrongId"
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
                userReturned = null
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
                nonHumanAnimalInfoEntityReturned = flowOf(null)
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
                allNonHumanAnimalsReturned = flowOf(emptyList())
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
            val checkFosterHomeViewmodel = getCheckFosterHomeViewmodel(
                fosterHomeId = "otherId",
                ownerId = "otherOwnerId"
            )

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

            val result = checkFosterHomeViewmodel.canIStartTheChat()

            assertTrue { result }
        }
}
