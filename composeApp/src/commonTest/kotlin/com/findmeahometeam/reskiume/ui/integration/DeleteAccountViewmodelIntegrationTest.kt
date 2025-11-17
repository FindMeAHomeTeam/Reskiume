package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeRealtimeDatabaseRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getDeleteAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localRepository: LocalRepository = FakeLocalRepository(),
        realtimeDatabaseRepository: RealtimeDatabaseRepository = FakeRealtimeDatabaseRepository(),
        storageRepository: StorageRepository = FakeStorageRepository()
    ): DeleteAccountViewmodel {

        val observeAuthStateFromAuthDataSource =
            ObserveAuthStateFromAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRepository)

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageInLocalDataSource =
            DeleteImageInLocalDataSource(storageRepository)

        val deleteUserFromLocalDataSource =
            DeleteUserFromLocalDataSource(localRepository)

        val log: Log = FakeLog()

        return DeleteAccountViewmodel(
            observeAuthStateFromAuthDataSource,
            getUserFromLocalDataSource,
            getUserFromRemoteDataSource,
            deleteUserFromAuthDataSource,
            deleteUserFromRemoteDataSource,
            deleteImageFromRemoteDataSource,
            deleteImageInLocalDataSource,
            deleteUserFromLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when that user deletes their account using their password_then their account is deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(mutableListOf(user.toData())),
                localRepository = FakeLocalRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${user.uid}/${Paths.USERS.path}", user.image)
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "${user.uid}/${user.image}",
                            "local_path/${user.uid}/${Paths.USERS.path}"
                        )
                    )
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the auth repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel()
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the remote repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their account on local repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRepository = FakeRealtimeDatabaseRepository(mutableListOf(user.toData()))
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }
}
