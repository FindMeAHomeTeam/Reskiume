package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.CreateUserWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.InsertUserToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeRealtimeDatabaseRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCreateAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        realtimeDatabaseRepository: RealtimeDatabaseRepository = FakeRealtimeDatabaseRepository(),
        localRepository: LocalRepository = FakeLocalRepository()
    ): CreateAccountViewmodel {

        val createUserWithEmailAndPasswordFromAuthDataSource =
            CreateUserWithEmailAndPasswordFromAuthDataSource(authRepository)

        val insertUserToRemoteDataSource =
            InsertUserToRemoteDataSource(realtimeDatabaseRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertUserToLocalDataSource =
            InsertUserToLocalDataSource(localRepository)

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val log: Log = FakeLog()

        return CreateAccountViewmodel(
            createUserWithEmailAndPasswordFromAuthDataSource,
            insertUserToRemoteDataSource,
            uploadImageToRemoteDataSource,
            insertUserToLocalDataSource,
            deleteUserFromAuthDataSource,
            deleteUserFromRemoteDataSource,
            deleteImageFromRemoteDataSource,
            log
        )
    }

    @Test
    fun `given an unregistered user_when that user creates an account using email_then the account is created`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel()
            createAccountViewmodel.createUserUsingEmailAndPwd(user, userPwd)
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when that user creates an account using email but there is an error creating it_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                )
            )
            createAccountViewmodel.createUserUsingEmailAndPwd(user, userPwd)
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

        @Test
        fun `given an unregistered user_when that user creates an account using email but there is an error storing user data in the remote data sources_then the app displays an error`() =
            runTest {
                val createAccountViewmodel = getCreateAccountViewmodel(
                    realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(remoteUserList = mutableListOf(user.toData()))
                )
                createAccountViewmodel.createUserUsingEmailAndPwd(user, userPwd)
                createAccountViewmodel.state.test {
                    assertTrue { awaitItem() is UiState.Idle }
                    assertTrue { awaitItem() is UiState.Loading }
                    assertTrue { awaitItem() is UiState.Error }
                    ensureAllEventsConsumed()
                }
            }

        @Test
        fun `given an unregistered user_when that user creates an account using email but there is an error storing that user in the local datasource_then the app displays an error`() =
            runTest {
                val createAccountViewmodel = getCreateAccountViewmodel(
                    localRepository = FakeLocalRepository(localUserList = mutableListOf(user))
                )
                createAccountViewmodel.createUserUsingEmailAndPwd(user, userPwd)
                createAccountViewmodel.state.test {
                    assertTrue { awaitItem() is UiState.Idle }
                    assertTrue { awaitItem() is UiState.Loading }
                    assertTrue { awaitItem() is UiState.Error }
                    ensureAllEventsConsumed()
                }
            }
}
