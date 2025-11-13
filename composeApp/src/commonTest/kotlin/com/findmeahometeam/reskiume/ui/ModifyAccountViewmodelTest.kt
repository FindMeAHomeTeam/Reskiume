package com.findmeahometeam.reskiume.ui

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.RealtimeDatabaseRepository
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ModifyAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onUpdateUserEmailFromAuth = Capture.slot<(String) -> Unit>()

    private val onUpdateUserPwdFromAuth = Capture.slot<(String) -> Unit>()

    private val onModifyUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onUpdateRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onLocalImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onImageUploaded = Capture.slot<(String) -> Unit>()

    private fun getModifyAccountViewmodel(
        onUpdateUserEmailFromAuthErrorArg: String = "",
        onUpdateUserPwdFromAuthErrorArg: String = "",
        getUserReturn: User? = user,
        onModifyUserArg: Int = 1,
        getRemoteUserArg: RemoteUser? = user.toData(),
        onUpdateRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        onLocalImageDeletedArg: Boolean = true,
        onRemoteImageDeletedArg: Boolean = true,
        onImageUploadedArg: String = user.image,
    ): ModifyAccountViewmodel {

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authUser))
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

        val localRepository: LocalRepository = mock {
            everySuspend { getUser(user.uid) } returns getUserReturn
            everySuspend {
                modifyUser(
                    user,
                    capture(onModifyUserFromLocal)
                )
            } calls { onModifyUserFromLocal.get().invoke(onModifyUserArg) }
        }

        val realtimeDatabaseRepository: RealtimeDatabaseRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns flowOf(getRemoteUserArg)
            everySuspend {
                updateRemoteUser(
                    user.toData(),
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
                    Paths.USERS,
                    capture(onRemoteImageDeleted)
                )
            } calls { onRemoteImageDeleted.get().invoke(onRemoteImageDeletedArg) }

            every {
                uploadImage(
                    user.uid,
                    Paths.USERS,
                    user.image,
                    capture(onImageUploaded)
                )
            } calls { onImageUploaded.get().invoke(onImageUploadedArg) }
        }

        val observeAuthStateFromAuthDataSource =
            ObserveAuthStateFromAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRepository)

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
            ModifyUserFromRemoteDataSource(realtimeDatabaseRepository)

        val modifyUserFromLocalDataSource =
            ModifyUserFromLocalDataSource(localRepository)

        val signOutFromAuthDataSource =
            SignOutFromAuthDataSource(authRepository)

        val log: Log = mock {
            every { d(any(), any()) } returns Unit
            every { e(any(), any()) } returns Unit
        }

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
    fun `given a registered user_when that user deletes their account but the remote data source fails deleting their avatar_then the app displays an error`() =
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
    fun `given a registered user_when that user deletes their account but the local data source fails deleting their avatar_then the app displays an error`() =
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
    fun `given a registered user_when that user modifies their account but the remote repository fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onUpdateRemoteUserArg = DatabaseResult.Error("Error updating remote user")
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
    fun `given a registered user_when that user modifies their account but the local repository fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                onModifyUserArg = 0,
                getRemoteUserArg = user.copy(image = "").toData(),
                onImageUploadedArg = ""
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = false,
                isDifferentImage = true,
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
}
