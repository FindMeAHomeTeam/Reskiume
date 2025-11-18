package com.findmeahometeam.reskiume.ui

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
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
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onDeleteUserFromAuth = Capture.slot<(String) -> Unit>()

    private val onImageUploaded = Capture.slot<(String) -> Unit>()

    private val onImageDeletedFromRemote = Capture.slot<(Boolean) -> Unit>()

    private val onSuccessRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertUserFromLocal = Capture.slot<(Long) -> Unit>()

    private fun getCreateAccountViewmodel(
        createUserWithEmailAndPasswordResult: AuthResult = AuthResult.Success(authUser),
        onDeleteUserErrorArg: String = "",
        onImageUploadedArg: String = user.image,
        onImageDeletedArg: Boolean = true,
        insertRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        deleteRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        onInsertUserArg: Long = 1L
    ): CreateAccountViewmodel {
        val authRepository: AuthRepository = mock {
            everySuspend {
                createUserWithEmailAndPassword(
                    user.email!!,
                    userPwd
                )
            } returns createUserWithEmailAndPasswordResult

            everySuspend { deleteUser(any(), capture(onDeleteUserFromAuth)) } calls {
                onDeleteUserFromAuth.get().invoke(onDeleteUserErrorArg)
            }
        }

        val storageRepository: StorageRepository = mock {
            every {
                uploadImage(
                    any(),
                    any(),
                    any(),
                    capture(onImageUploaded)
                )
            } calls { onImageUploaded.get().invoke(onImageUploadedArg) }

            everySuspend {
                deleteRemoteImage(
                    any(),
                    any(),
                    capture(onImageDeletedFromRemote)
                )
            } calls { onImageDeletedFromRemote.get().invoke(onImageDeletedArg) }
        }

        val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
            everySuspend {
                insertRemoteUser(
                    any(),
                    capture(onSuccessRemoteUser)
                )
            } calls { onSuccessRemoteUser.get().invoke(insertRemoteUserArg) }

            every {
                deleteRemoteUser(
                    any(),
                    capture(onSuccessRemoteUser)
                )
            } calls { onSuccessRemoteUser.get().invoke(deleteRemoteUserArg) }
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend { insertUser(any(), capture(onInsertUserFromLocal)) } calls {
                onInsertUserFromLocal.get().invoke(onInsertUserArg)
            }
        }

        val createUserWithEmailAndPasswordFromAuthDataSource =
            CreateUserWithEmailAndPasswordFromAuthDataSource(authRepository)

        val insertUserToRemoteDataSource =
            InsertUserToRemoteDataSource(realtimeDatabaseRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertUserToLocalDataSource =
            InsertUserToLocalDataSource(localUserRepository)

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val log: Log = mock {
            every { d(any(), any()) } returns Unit
            every { e(any(), any()) } returns Unit
        }

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
                createUserWithEmailAndPasswordResult = AuthResult.Error("error"),
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
    fun `given an unregistered user_when that user creates an account using email but there is an error storing user data and deleting their avatar in the remote data sources_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                onImageUploadedArg = "",
                insertRemoteUserArg = DatabaseResult.Error("error"),
                onImageDeletedArg = false
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
    fun `given an unregistered user_when that user creates an account using email but there is an error storing user data and deleting their data in the remote data sources_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                onImageUploadedArg = "",
                insertRemoteUserArg = DatabaseResult.Error("error"),
                onImageDeletedArg = false,
                onDeleteUserErrorArg = "error"
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
                onInsertUserArg = 0
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
    fun `given an unregistered user_when that user creates an account using email but there is an error storing that user in the local datasource and deleting their data in the remote datasource_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                onInsertUserArg = 0,
                deleteRemoteUserArg = DatabaseResult.Error("error")
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
