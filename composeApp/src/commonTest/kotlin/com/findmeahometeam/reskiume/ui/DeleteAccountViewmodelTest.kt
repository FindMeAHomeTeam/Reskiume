package com.findmeahometeam.reskiume.ui

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
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
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onDeleteUserFromAuth = Capture.slot<(String) -> Unit>()

    private val onDeleteUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onSuccessDeleteRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onLocalImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private fun getDeleteAccountViewmodel(
        authStateResult: AuthUser? = authUser,
        deleteUserFromAuthErrorArg: String = "",
        getUserResult: User = user,
        deleteUserFromLocalArg: Int = 1,
        remoteUserResult: RemoteUser? = user.toData(),
        successRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        remoteImageDeletedArg: Boolean = true,
        localImageDeletedArg: Boolean = true
    ): DeleteAccountViewmodel {

        val authRepository: AuthRepository = mock {
            every { authState } returns flowOf(authStateResult)

            everySuspend { deleteUser(any(), capture(onDeleteUserFromAuth)) } calls {
                onDeleteUserFromAuth.get().invoke(deleteUserFromAuthErrorArg)
            }
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend { getUser(user.uid) } returns getUserResult
            everySuspend {
                deleteUser(
                    user.uid,
                    capture(onDeleteUserFromLocal)
                )
            } calls { onDeleteUserFromLocal.get().invoke(deleteUserFromLocalArg) }
        }

        val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns flowOf(remoteUserResult)

            every {
                deleteRemoteUser(
                    user.uid,
                    capture(onSuccessDeleteRemoteUser)
                )
            } calls { onSuccessDeleteRemoteUser.get().invoke(successRemoteUserArg) }
        }

        val storageRepository: StorageRepository = mock {
            everySuspend {
                deleteRemoteImage(
                    user.uid,
                    Paths.USERS,
                    capture(onRemoteImageDeleted)
                )
            } calls { onRemoteImageDeleted.get().invoke(remoteImageDeletedArg) }

            every {
                deleteLocalImage(
                    user.uid,
                    user.image,
                    capture(onLocalImageDeleted)
                )
            } calls { onLocalImageDeleted.get().invoke(localImageDeletedArg) }
        }

        val observeAuthStateFromAuthDataSource =
            ObserveAuthStateFromAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

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
            DeleteUserFromLocalDataSource(localUserRepository)

        val log: Log = mock {
            every { d(any(), any()) } returns Unit
            every { e(any(), any()) } returns Unit
        }

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
            val deleteAccountViewmodel = getDeleteAccountViewmodel()
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
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authStateResult = null
            )
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
                remoteUserResult = null
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
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their account on remote repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                successRemoteUserArg = DatabaseResult.Error("error")
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
    fun `given a registered user_when that user deletes their account using their password but their avatar deletion fails in data sources_then their account is deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                remoteImageDeletedArg = false,
                localImageDeletedArg = false
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
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their account on local and auth repositories_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                getUserResult = user.copy(image = ""),
                deleteUserFromAuthErrorArg = "error",
                deleteUserFromLocalArg = 0
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
