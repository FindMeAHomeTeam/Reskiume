package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountViewmodel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LoginAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCache = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyLocalCache = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onInsertUserFromLocal = Capture.slot<(Long) -> Unit>()

    private val onModifyUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onSaveImageToLocal = Capture.slot<(String) -> Unit>()

    private fun getLoginAccountViewmodel(
        signInWithEmailAndPasswordResult: AuthResult = AuthResult.Success(authUser),
        cacheArg: LocalCache = localCache.copy(section = Section.USERS),
        getLocalCacheEntityReturn: LocalCacheEntity? = localCache.copy(section = Section.USERS).toEntity(),
        rowIdInsertedCacheArg: Long = 1L,
        rowsUpdatedCacheArg: Int = 1,
        rowIdInsertedUserArg: Long = 1L,
        rowsUpdatedUserArg: Int = 1,
        remoteUserResult: RemoteUser? = user.toData(),
        absolutePathArg: String = user.image,
    ): LoginAccountViewmodel {
        val authRepository: AuthRepository = mock {

            every {
                authState
            } returns flowOf(authUser)

            everySuspend {
                signInWithEmailAndPassword(
                    user.email!!,
                    userPwd
                )
            } returns signInWithEmailAndPasswordResult
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                getLocalCacheEntity(
                    cacheArg.cachedObjectId,
                    cacheArg.section
                )
            } returns getLocalCacheEntityReturn

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCache)
                )
            } calls { onInsertLocalCache.get().invoke(rowIdInsertedCacheArg) }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCache)
                )
            } calls { onModifyLocalCache.get().invoke(rowsUpdatedCacheArg) }
        }

        val localUserRepository: LocalUserRepository = mock {

            everySuspend { insertUser(any(), capture(onInsertUserFromLocal)) } calls {
                onInsertUserFromLocal.get().invoke(rowIdInsertedUserArg)
            }
            everySuspend { modifyUser(any(), capture(onModifyUserFromLocal)) } calls {
                onModifyUserFromLocal.get().invoke(rowsUpdatedUserArg)
            }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
            every { getRemoteUser(user.uid) } returns flowOf(remoteUserResult)
        }

        val storageRepository: StorageRepository = mock {

            every {
                downloadImage(
                    user.uid,
                    "",
                    Section.USERS,
                    capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathArg) }
        }

        val log: Log = mock {
            every { d(any(), any()) } returns Unit
            every { e(any(), any()) } returns Unit
        }

        val konnectivity: Konnectivity = mock {
            every { isConnected } returns true
            every { currentNetworkConnection } returns NetworkConnection.WIFI
            every { isConnectedState } returns MutableStateFlow(true)
            every { currentNetworkConnectionState } returns MutableStateFlow(NetworkConnection.WIFI)
        }

        val signInWithEmailAndPasswordFromAuthDataSource =
            SignInWithEmailAndPasswordFromAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(localUserRepository, authRepository)

        val modifyUserInLocalDataSource =
            ModifyUserInLocalDataSource(localUserRepository, authRepository)


        return LoginAccountViewmodel(
            signInWithEmailAndPasswordFromAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getUserFromRemoteDataSource,
            downloadImageToLocalDataSource,
            insertUserInLocalDataSource,
            modifyUserInLocalDataSource,
            log
        )
    }

    @Test
    fun `given an unregistered user_when they log in using their email with the local data source empty_then they gain access to their account`() =
        runTest {
            val loginAccountViewmodel = getLoginAccountViewmodel(
                getLocalCacheEntityReturn = null
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
                getLocalCacheEntityReturn = null,
                rowIdInsertedUserArg = 0
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
                getLocalCacheEntityReturn = null,
                remoteUserResult = null
            )
            loginAccountViewmodel.signInUsingEmail(user.email!!, userPwd)
            loginAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when they log in using their email after 24h_then they gain access to their account`() =
        runTest {

            val loginAccountViewmodel = getLoginAccountViewmodel(
                getLocalCacheEntityReturn = localCache.copy(section = Section.USERS, timestamp = 123L).toEntity(),
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

    @Test
    fun `given an unregistered user_when they log in to their account with no avatar using their email after 24h_then they gain access to it`() =
        runTest {

            val loginAccountViewmodel = getLoginAccountViewmodel(
                remoteUserResult = user.copy(image = "").toData(),
                getLocalCacheEntityReturn = localCache.copy(section = Section.USERS, timestamp = 123L).toEntity()
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

            val loginAccountViewmodel = getLoginAccountViewmodel(
                remoteUserResult = user.toData(),
                getLocalCacheEntityReturn = localCache.copy(section = Section.USERS, timestamp = 123L).toEntity(),
                rowsUpdatedUserArg = 0
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
