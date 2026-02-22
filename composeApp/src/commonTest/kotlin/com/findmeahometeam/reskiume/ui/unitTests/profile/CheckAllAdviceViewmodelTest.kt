package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Advice
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.AdviceType
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.CheckAllAdviceViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.careAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.rehomeAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.rescueAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckAllAdviceViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCacheEntity = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onInsertUserInLocal = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyUserInLocal = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onSaveImageToLocal = Capture.slot<(String) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private val konnectivity: Konnectivity = mock {
        every { isConnected } returns true
        every { currentNetworkConnection } returns NetworkConnection.WIFI
        every { isConnectedState } returns MutableStateFlow(true)
        every { currentNetworkConnectionState } returns MutableStateFlow(NetworkConnection.WIFI)
    }

    private fun getCheckAllAdviceViewmodel(
        authStateReturn: AuthUser? = authUser,
        getLocalCacheEntityReturn: LocalCacheEntity? =
            localCache.copy(section = Section.USERS).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        absolutePathArg: String = user.image,
        getRemoteUserReturn: Flow<RemoteUser> = flowOf(user.toData()),
        rowIdInsertedUserArg: Long = 1L,
        rowsUpdatedUserArg: Int = 1
    ): CheckAllAdviceViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.USERS
                )
            } returns getLocalCacheEntityReturn

            everySuspend {
                getLocalCacheEntity(
                    "wrongId",
                    Section.USERS
                )
            } returns null

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(localCacheIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCacheEntity)
                )
            } calls { onModifyLocalCacheEntity.get().invoke(localCacheUpdatedInLocalDatasourceArg) }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns getRemoteUserReturn

            every {
                getRemoteUser("wrongId")
            } returns flowOf(null)
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend {
                getUser(user.uid)
            } returns user

            everySuspend {
                getUser("wrongId")
            } returns null

            everySuspend { insertUser(any(), capture(onInsertUserInLocal)) } calls {
                onInsertUserInLocal.get().invoke(rowIdInsertedUserArg)
            }
            everySuspend { modifyUser(any(), capture(onModifyUserInLocal)) } calls {
                onModifyUserInLocal.get().invoke(rowsUpdatedUserArg)
            }
        }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    userUid = user.uid,
                    section = Section.USERS,
                    onImageSaved = capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathArg) }
        }


        val manageImagePath: ManageImagePath = mock {
            every { getImagePathForFileName(user.image) } returns user.image
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(manageImagePath, localUserRepository, authRepository)

        val modifyUserInLocalDataSource =
            ModifyUserInLocalDataSource(manageImagePath, localUserRepository, authRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val checkActivistUtil = CheckActivistUtil(
            getDataByManagingObjectLocalCacheTimestamp,
            getUserFromRemoteDataSource,
            getUserFromLocalDataSource,
            downloadImageToLocalDataSource,
            insertUserInLocalDataSource,
            modifyUserInLocalDataSource,
            getImagePathForFileNameFromLocalDataSource,
            log
        )

        val stringProvider: StringProvider = mock {
            everySuspend {
                getStringResource(any())
            } returns "I found a non-human animal in the street. What can I do?"
        }

        return CheckAllAdviceViewmodel(
            observeAuthStateInAuthDataSource,
            checkActivistUtil,
            stringProvider
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when the registered user clicks on the all section_then the app display advice about helping non human animals`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel()

            checkAllAdviceViewmodel.updateAdviceList(AdviceType.ALL)

            runCurrent()

            checkAllAdviceViewmodel.adviceListState.test {
                assertEquals(
                    UiState.Success(rescueAdviceList + rehomeAdviceList + careAdviceList),
                    awaitItem()
                )
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a non registered user_when the non registered user clicks on the rescue section_then the app display advice about rescuing non human animals`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authStateReturn = null
            )

            checkAllAdviceViewmodel.updateAdviceList(AdviceType.RESCUE)

            runCurrent()

            checkAllAdviceViewmodel.adviceListState.test {
                assertEquals(UiState.Success(rescueAdviceList), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a non registered user_when the non registered user clicks on the rehome section_then the app display advice about rehoming non human animals`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authStateReturn = null
            )

            checkAllAdviceViewmodel.updateAdviceList(AdviceType.REHOME)

            runCurrent()

            checkAllAdviceViewmodel.adviceListState.test {
                assertEquals(UiState.Success(rehomeAdviceList), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when the registered user clicks on the care section_then the app display advice about caregiving non human animals`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel()

            checkAllAdviceViewmodel.updateAdviceList(AdviceType.CARE)

            runCurrent()

            checkAllAdviceViewmodel.adviceListState.test {
                assertEquals(UiState.Success(careAdviceList), awaitItem())
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when the registered user search for advice_then the app display advice related to the search`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel()

            checkAllAdviceViewmodel.searchAdvice("non-human animal")

            runCurrent()

            checkAllAdviceViewmodel.adviceListState.test {
                assertTrue { (awaitItem() as UiState.Success<List<Advice>>).data.isNotEmpty() }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a non registered user_when the app checks their registered status_then the app wont display the send advice button`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authStateReturn = null
            )
            checkAllAdviceViewmodel.checkAuthState { isLoggedIn ->
                assertFalse { isLoggedIn }
            }
        }

    @Test
    fun `given an advice_when the app checks the author_then the app retrieve their data`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authStateReturn = null
            )
            checkAllAdviceViewmodel.retrieveAdviceAuthor(user.uid) { actualUser ->
                assertEquals(user, actualUser)
            }
        }

    @Test
    fun `given an advice_when the app checks the author but their id is wrong_then the app wont retrieve their data`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authStateReturn = null
            )
            checkAllAdviceViewmodel.retrieveAdviceAuthor("wrongId") { actualUser ->
                assertEquals(null, actualUser)
            }
        }
}
