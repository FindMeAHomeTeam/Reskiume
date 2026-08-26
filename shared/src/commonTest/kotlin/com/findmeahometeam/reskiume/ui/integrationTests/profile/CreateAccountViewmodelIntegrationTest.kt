package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFCMSubscriberRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CreateAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getCreateAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        manageImagePath: ManageImagePath = FakeManageImagePath(),
        fcmSubscriberRepository: FCMSubscriberRepository = FakeFCMSubscriberRepository(),
        log: Log = FakeLog()
    ): CreateAccountViewmodel {

        val createUserWithEmailAndPasswordInAuthDataSource =
            CreateUserWithEmailAndPasswordInAuthDataSource(authRepository)

        val insertUserInRemoteDataSource =
            InsertUserInRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val insertCacheInLocalRepository =
            InsertCacheInLocalRepository(localCacheRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(
                authRepository,
                manageImagePath,
                localUserRepository,
                fcmSubscriberRepository,
                log
            )

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val log: Log = FakeLog()

        return CreateAccountViewmodel(
            createUserWithEmailAndPasswordInAuthDataSource,
            insertUserInRemoteDataSource,
            uploadImageToRemoteDataSource,
            insertCacheInLocalRepository,
            insertUserInLocalDataSource,
            deleteUserFromAuthDataSource,
            deleteUserFromRemoteDataSource,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            log
        )
    }

    @Test
    fun `given an unregistered user_when that user creates an account using email_then the account is created`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel()
            createAccountViewmodel.saveUserChanges(user, userPwd, user.subscriptions[0].topic)
            createAccountViewmodel.state.test {
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
            createAccountViewmodel.saveUserChanges(user, userPwd, user.subscriptions[0].topic)
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when that user creates an account using email without a subscription_then the account is created`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel()
            createAccountViewmodel.saveUserChanges(user, userPwd, "")
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when that user creates an account using email but there is an error storing user data in the remote data sources_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    remoteUserList = mutableListOf(user.toData())
                )
            )
            createAccountViewmodel.saveUserChanges(user, userPwd, user.subscriptions[0].topic)
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an unregistered user_when that user creates an account using email but there is an error storing that user in the local datasource_then the app displays an error`() =
        runTest {
            val createAccountViewmodel = getCreateAccountViewmodel(
                localUserRepository = FakeLocalUserRepository(
                    localUserWithAllSubscriptionDataList = mutableListOf(
                        userWithAllSubscriptionData
                    )
                )
            )
            createAccountViewmodel.saveUserChanges(user, userPwd, user.subscriptions[0].topic)
            createAccountViewmodel.state.test {
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given an image to discard_when the user clicks on the delete button_then the image is discarded`() =
        runTest {
            val storageRepository = FakeStorageRepository(
                localDatasourceList = mutableListOf(
                    Pair(
                        "local_path",
                        user.image
                    )
                )
            )
            val createAccountViewmodel = getCreateAccountViewmodel(
                storageRepository = storageRepository
            )
            createAccountViewmodel.deleteLocalImage(user.image)

            assertTrue { storageRepository.localDatasourceList.isEmpty() }
        }
}
