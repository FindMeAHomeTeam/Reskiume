package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
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
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStringProvider
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.AdviceType
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.CheckAllAdviceViewmodel
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.careAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.rehomeAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.rescueAdviceList
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckAllAdviceViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCheckAllAdviceViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(
            authUser = authUser,
            authEmail = user.email,
            authPassword = userPwd
        ),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        stringProvider: StringProvider = FakeStringProvider("I found a non-human animal in the street. What can I do?"),
        manageImagePath: ManageImagePath = FakeManageImagePath()
    ): CheckAllAdviceViewmodel {

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
                authRepository = FakeAuthRepository()
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
                authRepository = FakeAuthRepository()
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
                authRepository = FakeAuthRepository()
            )
            checkAllAdviceViewmodel.checkAuthState { isLoggedIn ->
                assertFalse { isLoggedIn }
            }
        }

    @Test
    fun `given an advice_when the app checks the author_then the app retrieve their data`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authRepository = FakeAuthRepository(),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user))
            )
            checkAllAdviceViewmodel.retrieveAdviceAuthor(user.uid) { actualUser ->
                assertEquals(user.copy(savedBy = "", email = null), actualUser)
            }
        }

    @Test
    fun `given an advice_when the app checks the author but their id is wrong_then the app wont retrieve their data`() =
        runTest {
            val checkAllAdviceViewmodel = getCheckAllAdviceViewmodel(
                authRepository = FakeAuthRepository()
            )
            checkAllAdviceViewmodel.retrieveAdviceAuthor("wrongId") { actualUser ->
                assertEquals(null, actualUser)
            }
        }
}
