package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoginAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getLoginAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        konnectivity: Konnectivity = FakeKonnectivity()
    ): LoginAccountViewmodel {

        val signInWithEmailAndPasswordFromAuthDataSource =
            SignInWithEmailAndPasswordFromAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(manageImagePath, localUserRepository, authRepository)

        val modifyUserInLocalDataSource =
            ModifyUserInLocalDataSource(manageImagePath, localUserRepository, authRepository)

        val log: Log = FakeLog()

        return LoginAccountViewmodel(
            signInWithEmailAndPasswordFromAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getUserFromRemoteDataSource,
            downloadImageToLocalDataSource,
            insertUserInLocalDataSource,
            modifyUserInLocalDataSource,
            log
        )
    }

    @Test
    fun `given an unregistered user_when they log in using their email with the local data source empty_then they gain access to their account`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                )
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email but the app fails retrieving the data from the auth repository_then the app displays an error`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel()
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email after 24h_then they gain access to their account`() =
        runTest {

            val loginAccountViewmodel = getLoginAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(section = Section.USERS, timestamp = 123L).toEntity()
                    )
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                )
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email_then they gain access to their account`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(section = Section.USERS).toEntity()
                    )
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                )
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
