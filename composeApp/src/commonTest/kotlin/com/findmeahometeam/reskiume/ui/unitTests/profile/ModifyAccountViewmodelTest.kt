package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.remoterUser.RemoteUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.repository.util.fcm.FCMSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ModifyAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onUpdateUserEmailFromAuth = Capture.slot<(String) -> Unit>()

    private val onUpdateUserPwdFromAuth = Capture.slot<(String) -> Unit>()

    private val onModifyUserInLocal = Capture.slot<suspend (Int) -> Unit>()

    private val onUpdateRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onLocalImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onImageUploaded = Capture.slot<(String) -> Unit>()

    private val onModifyLocalCache = Capture.slot<(rowsUpdated: Int) -> Unit>()


    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getModifyAccountViewmodel(
        authStateResult: AuthUser? = authUser,
        onUpdateUserEmailFromAuthErrorArg: String = "",
        onUpdateUserPwdFromAuthErrorArg: String = "",
        getUserReturn: Flow<UserWithAllSubscriptionData?> = flowOf(userWithAllSubscriptionData),
        modifyUserArg: UserEntity = user.toEntity(),
        onModifyUserArg: Int = 1,
        getRemoteUserReturn: RemoteUser? = user.toData(),
        onUpdateRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        onLocalImageDeletedArg: Boolean = true,
        onRemoteImageDeletedArg: Boolean = true,
        onImageUploadedArg: String = user.image,
        updatedRowsCacheArg: Int = 1,
        cacheArg: LocalCache = localCache.copy(section = Section.USERS),
        getLocalCacheEntityReturn: LocalCacheEntity? = localCache.copy(section = Section.USERS)
            .toEntity(),
        subscribeToTopicReturn: Flow<Boolean> = flowOf(true),
        unsubscribeToTopicReturn: Flow<Boolean> = flowOf(true)
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
                    capture(onModifyUserInLocal)
                )
            } calls { onModifyUserInLocal.get().invoke(onModifyUserArg) }

            everySuspend {
                insertSubscription(
                    any(),
                    any()
                )
            } returns Unit

            everySuspend {
                deleteSubscription(
                    user.subscriptions[0].subscriptionId,
                    any()
                )
            } returns Unit
        }

        val manageImagePath: ManageImagePath = mock {
            every { getImagePathForFileName(user.image) } returns user.image

            every { getFileNameFromLocalImagePath(user.image) } returns user.image

            every { getFileNameFromLocalImagePath("") } returns ""
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns flowOf(getRemoteUserReturn)

            everySuspend {
                updateRemoteUser(
                    any(),
                    capture(onUpdateRemoteUser)
                )
            } calls { onUpdateRemoteUser.get().invoke(onUpdateRemoteUserArg) }
        }

        val storageRepository: StorageRepository = mock {
            every {
                deleteLocalImage(
                    user.image,
                    capture(onLocalImageDeleted)
                )
            } calls { onLocalImageDeleted.get().invoke(onLocalImageDeletedArg) }

            everySuspend {
                deleteRemoteImage(
                    user.uid,
                    "",
                    Section.USERS,
                    capture(onRemoteImageDeleted)
                )
            } calls { onRemoteImageDeleted.get().invoke(onRemoteImageDeletedArg) }

            every {
                uploadImage(
                    user.uid,
                    "",
                    Section.USERS,
                    user.image,
                    capture(onImageUploaded)
                )
            } calls { onImageUploaded.get().invoke(onImageUploadedArg) }
        }

        val localCacheRepository: LocalCacheRepository = mock {
            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyLocalCache)
                )
            } calls { onModifyLocalCache.get().invoke(updatedRowsCacheArg) }

            everySuspend {
                getLocalCacheEntity(
                    cacheArg.cachedObjectId,
                    cacheArg.section
                )
            } returns getLocalCacheEntityReturn
        }

        val subscriptionManagerUtil: SubscriptionManagerUtil = mock {

            everySuspend {
                unsubscribeFromAllTopicsAfterLogOut(user.copy(email = null))
            } returns Unit
        }

        val fCMSubscriberRepository: FCMSubscriberRepository = mock {
            everySuspend { subscribeToTopic(user.subscriptions[0].topic) } returns subscribeToTopicReturn

            everySuspend { subscribeToTopic("SPAINSEVILLE") } returns subscribeToTopicReturn

            everySuspend { unsubscribeFromTopic(user.subscriptions[0].topic) } returns unsubscribeToTopicReturn
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getImagePathForFileNameFromLocalDataSource =
            GetImagePathForFileNameFromLocalDataSource(manageImagePath)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val modifyUserEmailInAuthDataSource =
            ModifyUserEmailInAuthDataSource(authRepository)

        val modifyUserPasswordInAuthDataSource =
            ModifyUserPasswordInAuthDataSource(authRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val uploadImageToRemoteDataSource =
            UploadImageToRemoteDataSource(storageRepository)

        val modifyUserInRemoteDataSource =
            ModifyUserInRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val modifyUserInLocalDataSource =
            ModifyUserInLocalDataSource(
                manageImagePath,
                fCMSubscriberRepository,
                localUserRepository,
                authRepository,
                log
            )

        val modifyCacheInLocalRepository =
            ModifyCacheInLocalRepository(localCacheRepository)

        val signOutFromAuthDataSource =
            SignOutFromAuthDataSource(authRepository)

        return ModifyAccountViewmodel(
            observeAuthStateInAuthDataSource,
            getUserFromLocalDataSource,
            getImagePathForFileNameFromLocalDataSource,
            getUserFromRemoteDataSource,
            modifyUserEmailInAuthDataSource,
            modifyUserPasswordInAuthDataSource,
            deleteImageFromLocalDataSource,
            deleteImageFromRemoteDataSource,
            uploadImageToRemoteDataSource,
            modifyUserInRemoteDataSource,
            modifyUserInLocalDataSource,
            modifyCacheInLocalRepository,
            signOutFromAuthDataSource,
            subscriptionManagerUtil,
            log
        )
    }

    @Test
    fun `given a registered user_when that user modifies their account_then the account is updated with its new rescue event subscription`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                updatedPassword = "123456",
                shouldUpdateNotificationArea = true,
                previousNotificationArea = "UNSELECTEDUNSELECTED",
                updatedNotificationArea = user.subscriptions[0].topic
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User ${user.uid} updated successfully in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account without changing the rescue event subscription_then the account is updated`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                updatedPassword = "123456",
                shouldUpdateNotificationArea = true,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User ${user.uid} updated successfully in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account deleting the current rescue event subscription_then the account is updated`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = "UNSELECTEDUNSELECTED"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User ${user.uid} updated successfully in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user modifies their account updating the current rescue event subscription_then the account is updated`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel()
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                updatedPassword = "123456",
                shouldUpdateNotificationArea = true,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = "SPAINSEVILLE"
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User ${user.uid} updated successfully in the local data source"
                )
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
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
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
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
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
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
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
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic

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
                getUserReturn = flowOf(userWithAllSubscriptionData.copy(userEntity = user.toEntity())),
                modifyUserArg = user.toEntity(),
                getRemoteUserReturn = user.toData(),
                onImageUploadedArg = ""
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = true,
                isDifferentImage = true,
                user = user,
                currentPassword = userPwd,
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
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
                updatedPassword = "123456",
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
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
                onModifyUserArg = 0,
            )
            modifyAccountViewmodel.saveUserChanges(
                isDifferentEmail = false,
                isDifferentImage = false,
                user = user,
                currentPassword = userPwd,
                shouldUpdateNotificationArea = false,
                previousNotificationArea = user.subscriptions[0].topic,
                updatedNotificationArea = user.subscriptions[0].topic
            )
            modifyAccountViewmodel.uiState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when that user logs out_then logD is called`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                modifyUserArg = user.copy(isLoggedIn = false).toEntity()
            )
            modifyAccountViewmodel.logOut()

            runCurrent()

            verify {
                log.d(
                    "ModifyAccountViewmodel",
                    "saveUserChangesInLocalDataSource: User ${user.uid} updated successfully in the local data source"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user_when that user logs out and the local cache fails updating the last log out_then logE is called`() =
        runTest {
            val modifyAccountViewmodel = getModifyAccountViewmodel(
                modifyUserArg = user.copy(isLoggedIn = false).toEntity(),
                updatedRowsCacheArg = 0
            )
            modifyAccountViewmodel.logOut()

            runCurrent()

            verify {
                log.e(
                    "ModifyAccountViewmodel",
                    "modifyCacheInLocalRepo: Error updating ${user.uid} in the local cache in section USERS"
                )
            }
        }
}
