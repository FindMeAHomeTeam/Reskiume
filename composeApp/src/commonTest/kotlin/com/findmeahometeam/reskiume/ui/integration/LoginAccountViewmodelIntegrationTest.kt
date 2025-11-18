package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.SaveImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeRealtimeDatabaseRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoginAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getLoginAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        realtimeDatabaseRepository: RealtimeDatabaseRepository = FakeRealtimeDatabaseRepository(),
        storageRepository: StorageRepository = FakeStorageRepository()
    ): LoginAccountViewmodel {

        val signInWithEmailAndPasswordFromAuthDataSource =
            SignInWithEmailAndPasswordFromAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRepository)

        val saveImageToLocalDataSource =
            SaveImageToLocalDataSource(storageRepository)

        val insertUserToLocalDataSource =
            InsertUserToLocalDataSource(localUserRepository)

        val modifyUserFromLocalDataSource =
            ModifyUserFromLocalDataSource(localUserRepository)

        val log: Log = FakeLog()

        return LoginAccountViewmodel(
            signInWithEmailAndPasswordFromAuthDataSource,
            getUserFromLocalDataSource,
            getUserFromRemoteDataSource,
            saveImageToLocalDataSource,
            insertUserToLocalDataSource,
            modifyUserFromLocalDataSource,
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
                realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(mutableListOf(user.toData()))
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
            val user = user.copy(lastLogout = 1563041881L)

            val loginAccountViewmodel = getLoginAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(mutableListOf(user.toData()))
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
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(mutableListOf(user.toData()))
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
