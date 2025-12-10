package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
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
import dev.mokkery.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onDeleteUserFromAuth = Capture.slot<(String) -> Unit>()

    private val onImageUploaded = Capture.slot<(String) -> Unit>()

    private val onImageDeletedFromRemote = Capture.slot<(Boolean) -> Unit>()

    private val onInsertLocalCache = Capture.slot<(rowId: Long) -> Unit>()

    private val onSuccessRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onInsertUserFromLocal = Capture.slot<(Long) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } returns Unit
        every { e(any(), any()) } returns Unit
    }

    private fun getCreateAccountViewmodel(
        createUserWithEmailAndPasswordResult: AuthResult = AuthResult.Success(authUser),
        onDeleteUserErrorArg: String = "",
        onImageUploadedArg: String = user.image,
        onImageDeletedArg: Boolean = true,
        rowIdInsertedCacheArg : Long = 1L,
        insertRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        deleteRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        onInsertUserArg: Long = 1L
    ): CreateAccountViewmodel {
        val authRepository: AuthRepository = mock {

            every {
                authState
            } returns flowOf(authUser)

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
                    any(),
                    capture(onImageUploaded)
                )
            } calls { onImageUploaded.get().invoke(onImageUploadedArg) }

            everySuspend {
                deleteRemoteImage(
                    any(),
                    any(),
                    any(),
                    capture(onImageDeletedFromRemote)
                )
            } calls { onImageDeletedFromRemote.get().invoke(onImageDeletedArg) }
        }

        val localCacheRepository: LocalCacheRepository = mock {
            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCache)
                )
            } calls { onInsertLocalCache.get().invoke(rowIdInsertedCacheArg) }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
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

        val createUserWithEmailAndPasswordInAuthDataSource =
            CreateUserWithEmailAndPasswordInAuthDataSource(authRepository)

        val insertUserInRemoteDataSource =
            InsertUserInRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(localUserRepository, authRepository)

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        return CreateAccountViewmodel(
            createUserWithEmailAndPasswordInAuthDataSource,
            insertUserInRemoteDataSource,
            uploadImageToRemoteDataSource,
            insertCacheInLocalRepository,
            insertUserInLocalDataSource,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an unregistered user_when that user creates an account using email but there is an error storing the cache in the local datasource_then logE is called`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                rowIdInsertedCacheArg = 0
            )
            createAccountViewmodel.createUserUsingEmailAndPwd(user, userPwd)

            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                ensureAllEventsConsumed()
            }
            runCurrent()

            verify {
                log.e(
                    "CreateAccountViewmodel",
                    "Error adding user ${user.uid} cache to local repository"
                )
            }
        }
}
