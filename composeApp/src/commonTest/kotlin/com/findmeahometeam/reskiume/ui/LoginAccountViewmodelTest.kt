package com.findmeahometeam.reskiume.ui

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
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
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
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
import kotlin.time.ExperimentalTime

class LoginAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertUserFromLocal = Capture.slot<(Long) -> Unit>()

    private val onModifyUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onSaveImageToLocal = Capture.slot<(String) -> Unit>()

    private fun getLoginAccountViewmodel(
        signInWithEmailAndPasswordResult: AuthResult = AuthResult.Success(authUser),
        userResult: User? = user,
        insertedRowIdArg: Long = 1L,
        modifiedRowsArg: Int = 1,
        remoteUserResult: RemoteUser? = user.toData(),
        absolutePathArg: String = user.image,
    ): LoginAccountViewmodel {
        val authRepository: AuthRepository = mock {
            everySuspend {
                signInWithEmailAndPassword(
                    user.email!!,
                    userPwd
                )
            } returns signInWithEmailAndPasswordResult
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend { getUser(user.uid) } returns userResult
            everySuspend { insertUser(any(), capture(onInsertUserFromLocal)) } calls {
                onInsertUserFromLocal.get().invoke(insertedRowIdArg)
            }
            everySuspend { modifyUser(any(), capture(onModifyUserFromLocal)) } calls {
                onModifyUserFromLocal.get().invoke(modifiedRowsArg)
            }
        }

        val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
            every { getRemoteUser(user.uid) } returns flowOf(remoteUserResult)
        }

        val storageRepository: StorageRepository = mock {
            every {
                saveImage(
                    user.uid,
                    Paths.USERS,
                    capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathArg) }
        }

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

        val log: Log = mock {
            every { d(any(), any()) } returns Unit
            every { e(any(), any()) } returns Unit
        }

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
                userResult = null
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
            val loginAccountViewmodel = getLoginAccountViewmodel(
                signInWithEmailAndPasswordResult = AuthResult.Error("Error")
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email with the local data source empty but fails inserting the data_then the app displays an error`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel(
                userResult = null,
                insertedRowIdArg = 0
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email but fails retrieving the data from the remote data source_then the app displays an error`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel(
                userResult = null,
                remoteUserResult = null
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `given an unregistered user_when they log in using their email after 24h_then they gain access to their account`() =
        runTest {
            val user = user.copy(lastLogout = 1563041881L)

            val loginAccountViewmodel = getLoginAccountViewmodel(
                userResult = user,
                remoteUserResult = user.toData(),
                absolutePathArg = ""
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `given an unregistered user_when they log in to their account with no avatar using their email after 24h_then they gain access to it`() =
        runTest {
            val user = user.copy(image = "", lastLogout = 1563041881L)

            val loginAccountViewmodel = getLoginAccountViewmodel(
                userResult = user,
                remoteUserResult = user.toData(),
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
    fun `given an unregistered user_when they log in using their email after 24h but fails modifying the data_then the app displays an error`() =
        runTest {
            val user = user.copy(lastLogout = 1563041881L)
            val loginAccountViewmodel = getLoginAccountViewmodel(
                userResult = user,
                remoteUserResult = user.toData(),
                modifiedRowsArg = 0
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email_then they gain access to their account`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel()
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }
}
