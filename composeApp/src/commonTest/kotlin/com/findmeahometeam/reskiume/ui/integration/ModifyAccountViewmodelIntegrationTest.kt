package com.findmeahometeam.reskiume.ui.integration

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Paths
import com.findmeahometeam.reskiume.data.util.log.Log
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
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integration.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ModifyAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

    private fun getModifyAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        storageRepository: StorageRepository = FakeStorageRepository()
    ): ModifyAccountViewmodel {

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
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(mutableListOf(user.toData())),
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
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user))
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
    fun `given a registered user_when that user modifies their email but the app fails_then it displays an error`() =
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
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user modifies their password but the app fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = false,
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
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(mutableListOf(user.toData()))
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
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(mutableListOf(user.toData())),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${user.uid}/${Paths.USERS.path}", user.image)
                    )
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user))
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
                authRepository = FakeAuthRepository(
                    authUser = authUser.copy(photoUrl = user.image),
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(mutableListOf(user.toData())),
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
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user))
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
    fun `given a registered user_when that user modifies their account but the local repository fails_then it displays an error`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser.copy(photoUrl = user.image),
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(mutableListOf(user.toData()))
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
}
