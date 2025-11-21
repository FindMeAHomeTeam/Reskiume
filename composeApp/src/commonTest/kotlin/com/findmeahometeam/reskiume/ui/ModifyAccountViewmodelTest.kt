package com.findmeahometeam.reskiume.ui

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.DeleteImageInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ModifyAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onUpdateUserEmailFromAuth = Capture.slot<(String) -> Unit>()

    private val onUpdateUserPwdFromAuth = Capture.slot<(String) -> Unit>()

    private val onModifyUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onUpdateRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onLocalImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onImageUploaded = Capture.slot<(String) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } returns Unit
        every { e(any(), any()) } returns Unit
    }

    private fun getModifyAccountViewmodel(
        authStateResult: AuthUser? = authUser,
        onUpdateUserEmailFromAuthErrorArg: String = "",
        onUpdateUserPwdFromAuthErrorArg: String = "",
        getUserReturn: User? = user,
        modifyUserArg: User = user,
        onModifyUserArg: Int = 1,
        getRemoteUserReturn: RemoteUser? = user.toData(),
        updateRemoteUserArg: RemoteUser = user.toData(),
        onUpdateRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        onLocalImageDeletedArg: Boolean = true,
        onRemoteImageDeletedArg: Boolean = true,
        onImageUploadedArg: String = user.image,
    ): ModifyAccountViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateResult))
            everySuspend {
                updateUserEmail(
                    userPwd,
                    user.email!!,
                    capture(onUpdateUserEmailFromAuth)
                )
            } calls { onUpdateUserEmailFromAuth.get().invoke(onUpdateUserEmailFromAuthErrorArg) }
            everySuspend {
                updateUserPassword(
                    userPwd,
                    "123456",
                    capture(onUpdateUserPwdFromAuth)
                )
            } calls { onUpdateUserPwdFromAuth.get().invoke(onUpdateUserPwdFromAuthErrorArg) }
            everySuspend { signOut() } returns true
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend { getUser(user.uid) } returns getUserReturn
            everySuspend {
                modifyUser(
                    modifyUserArg,
                    capture(onModifyUserFromLocal)
                )
            } calls { onModifyUserFromLocal.get().invoke(onModifyUserArg) }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns flowOf(getRemoteUserReturn)
            everySuspend {
                updateRemoteUser(
                    updateRemoteUserArg,
                    capture(onUpdateRemoteUser)
                )
            } calls { onUpdateRemoteUser.get().invoke(onUpdateRemoteUserArg) }
        }

        val storageRepository: StorageRepository = mock {
            every {
                deleteLocalImage(
                    user.uid,
                    user.image,
                    capture(onLocalImageDeleted)
                )
            } calls { onLocalImageDeleted.get().invoke(onLocalImageDeletedArg) }

            everySuspend {
                deleteRemoteImage(
                    user.uid,
                    Section.USERS,
                    capture(onRemoteImageDeleted)
                )
            } calls { onRemoteImageDeleted.get().invoke(onRemoteImageDeletedArg) }

            every {
                uploadImage(
                    user.uid,
                    Section.USERS,
                    user.image,
                    capture(onImageUploaded)
                )
            } calls { onImageUploaded.get().invoke(onImageUploadedArg) }
        }

        val observeAuthStateFromAuthDataSource =
            ObserveAuthStateFromAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val modifyUserEmailInAuthDataSource =
            ModifyUserEmailInAuthDataSource(authRepository)

        val modifyUserPasswordInAuthDataSource =
            ModifyUserPasswordInAuthDataSource(authRepository)

        val deleteImageInLocalDataSource =
            DeleteImageInLocalDataSource(storageRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val modifyUserFromRemoteDataSource =
            ModifyUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val modifyUserFromLocalDataSource =
            ModifyUserFromLocalDataSource(localUserRepository)

        val signOutFromAuthDataSource =
            SignOutFromAuthDataSource(authRepository)

        return ModifyAccountViewmodel(
            observeAuthStateFromAuthDataSource,
            getUserFromLocalDataSource,
            getUserFromRemoteDataSource,
            modifyUserEmailInAuthDataSource,
            modifyUserPasswordInAuthDataSource,
            deleteImageInLocalDataSource,
            deleteImageFromRemoteDataSource,
            uploadImageToRemoteDataSource,
            modifyUserFromRemoteDataSource,
            modifyUserFromLocalDataSource,
            signOutFromAuthDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when that user modifies their account_then the account is updated`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their email but the app fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onUpdateUserEmailFromAuthErrorArg = "Error updating email"
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their password but the app fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onUpdateUserPwdFromAuthErrorArg = "Error updating password"
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account but the remote data source fails to delete their avatar_then the app displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onRemoteImageDeletedArg = false
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account but the local data source fails to delete their avatar_then the app displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onLocalImageDeletedArg = false
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account with an empty avatar_then the account is updated`() =
        runTest {
            val user = user.copy(image = "")
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                getUserReturn = user,
                modifyUserArg = user,
                getRemoteUserReturn = user.toData(),
                updateRemoteUserArg = user.toData(),
                onImageUploadedArg = ""
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account but the remote repository fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onUpdateRemoteUserArg = DatabaseResult.Error("Error updating remote user")
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = false,
                user = user,
                currentPassword = userPwd,
                newPassword = "123456"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account but the local repository fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                getRemoteUserReturn = user.copy(image = "").toData(),
                updateRemoteUserArg = user.copy(image = "").toData(),
                onModifyUserArg = 0,
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = false,
                isDifferentImage = false,
                user = user,
                currentPassword = userPwd
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when that user logs out_then the user is unregistered`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                modifyUserArg = user.copy(lastLogout = Clock.System.now().epochSeconds)
            )
            modifyAccountViewmodel.logOut()

            runCurrent()

            verify {
                log.d(any(), any())
            }
        }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when that user logs out and the local datasource fails updating the last log out_then the app emit an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                modifyUserArg = user.copy(lastLogout = Clock.System.now().epochSeconds),
                onModifyUserArg = 0
            )
            modifyAccountViewmodel.logOut()

            runCurrent()

            verify {
                log.e(any(), any())
            }
        }
}
