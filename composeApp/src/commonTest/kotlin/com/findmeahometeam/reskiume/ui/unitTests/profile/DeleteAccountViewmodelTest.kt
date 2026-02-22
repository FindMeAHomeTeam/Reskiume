package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.review
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
import dev.mokkery.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteAccountViewmodelTest : CoroutineTestDispatcher() {

    private val onDeleteUserFromAuth = Capture.slot<(String) -> Unit>()

    private val onDeleteUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onDeleteAllNonHumanAnimalFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteAllNonHumanAnimalFromLocal = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteRemoteReviews = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteLocalReviews = Capture.slot<(Int) -> Unit>()

    private val onDeleteAllLocalCacheEntity = Capture.slot<(Int) -> Unit>()

    private val onSuccessDeleteRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onImageDeletedFromRemote = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onNonHumanAnimalImageDeletedFromLocal = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onUserImageDeletedFromLocal = Capture.slot<(Boolean) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getDeleteAccountViewmodel(
        authStateResult: AuthUser? = authUser,
        deleteUserFromAuthErrorArg: String = "",
        getUserResult: User = user,
        deleteUserFromLocalArg: Int = 1,
        remoteUserResult: RemoteUser? = user.toData(),
        databaseResultAfterDeletingAllRemoteNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        rowsDeletedOfAllNonHumanAnimalsArg: Int = 1,
        getRemoteReviewsResult: List<Review> = listOf(review),
        deleteRemoteReviewsArg: DatabaseResult = DatabaseResult.Success,
        deleteLocalReviewsArg: Int = 1,
        deleteLocalCacheEntityArg: Int = 1,
        successRemoteUserArg: DatabaseResult = DatabaseResult.Success,
        remoteImageDeletedArg: Boolean = true,
        remoteNonHumanAnimalImageDeletedArg: Boolean = true,
        localImageDeletedArg: Boolean = true,
        localNonHumanAnimalImageDeletedArg: Boolean = true
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
                deleteUsers(
                    user.uid,
                    capture(onDeleteUserFromLocal)
                )
            } calls { onDeleteUserFromLocal.get().invoke(deleteUserFromLocalArg) }
        }

        val realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = mock {

            every { getRemoteReviews(user.uid) } returns flowOf(getRemoteReviewsResult.map { it.toData() })

            every {
                deleteRemoteReviews(
                    user.uid,
                    capture(onDeleteRemoteReviews)
                )
            } calls { onDeleteRemoteReviews.get().invoke(deleteRemoteReviewsArg) }
        }

        val localReviewRepository: LocalReviewRepository = mock {

            everySuspend {
                deleteLocalReviews(
                    user.uid,
                    capture(onDeleteLocalReviews)
                )
            } calls { onDeleteLocalReviews.get().invoke(deleteLocalReviewsArg) }
        }

        val localCacheRepository: LocalCacheRepository = mock {

            everySuspend {
                deleteAllLocalCacheEntity(
                    user.uid,
                    capture(onDeleteAllLocalCacheEntity)
                )
            } calls { onDeleteAllLocalCacheEntity.get().invoke(deleteLocalCacheEntityArg) }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {

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
                    userUid = user.uid,
                    extraId = "",
                    section = Section.USERS,
                    onImageDeleted = capture(onRemoteImageDeleted)
                )
            } calls { onRemoteImageDeleted.get().invoke(remoteImageDeletedArg) }

            everySuspend {
                deleteRemoteImage(
                    nonHumanAnimal.caregiverId,
                    nonHumanAnimal.id,
                    Section.NON_HUMAN_ANIMALS,
                    capture(onImageDeletedFromRemote)
                )
            } calls { onImageDeletedFromRemote.get().invoke(remoteNonHumanAnimalImageDeletedArg) }

            every {
                deleteLocalImage(
                    user.image,
                    capture(onUserImageDeletedFromLocal)
                )
            } calls { onUserImageDeletedFromLocal.get().invoke(localImageDeletedArg) }

            every {
                deleteLocalImage(
                    nonHumanAnimal.imageUrl,
                    capture(onNonHumanAnimalImageDeletedFromLocal)
                )
            } calls { onNonHumanAnimalImageDeletedFromLocal.get().invoke(localNonHumanAnimalImageDeletedArg) }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = mock {

            every {
                getAllRemoteNonHumanAnimals(user.uid)
            } returns flowOf(listOf(nonHumanAnimal.toData()))

            every {
                deleteAllRemoteNonHumanAnimals(
                    nonHumanAnimal.caregiverId,
                    capture(onDeleteAllNonHumanAnimalFromRemote)
                )
            } calls { onDeleteAllNonHumanAnimalFromRemote.get().invoke(databaseResultAfterDeletingAllRemoteNonHumanAnimalArg) }
        }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            everySuspend {
                deleteAllNonHumanAnimals(
                    nonHumanAnimal.caregiverId,
                    capture(onDeleteAllNonHumanAnimalFromLocal)
                )
            } calls {
                onDeleteAllNonHumanAnimalFromLocal.get().invoke(rowsDeletedOfAllNonHumanAnimalsArg)
            }
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getAllNonHumanAnimalsFromRemoteRepository =
            GetAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val getReviewsFromRemoteRepository =
            GetReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

        val deleteReviewsFromRemoteRepository =
            DeleteReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

        val deleteReviewsFromLocalRepository =
            DeleteReviewsFromLocalRepository(localReviewRepository)

        val deleteAllCacheFromLocalRepository = DeleteAllCacheFromLocalRepository(localCacheRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val deleteUserFromAuthDataSource =
            DeleteUserFromAuthDataSource(authRepository)

        val deleteUserFromRemoteDataSource =
            DeleteUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val deleteImageFromRemoteDataSource =
            DeleteImageFromRemoteDataSource(storageRepository)

        val deleteImageFromLocalDataSource =
            DeleteImageFromLocalDataSource(storageRepository)

        val deleteUsersFromLocalDataSource =
            DeleteUsersFromLocalDataSource(localUserRepository)

        val deleteAllNonHumanAnimalsFromRemoteRepository =
            DeleteAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val deleteAllNonHumanAnimalsFromLocalRepository =
            DeleteAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        return DeleteAccountViewmodel(
            observeAuthStateInAuthDataSource,
            getAllNonHumanAnimalsFromRemoteRepository,
            deleteAllNonHumanAnimalsFromRemoteRepository,
            deleteAllNonHumanAnimalsFromLocalRepository,
            getReviewsFromRemoteRepository,
            deleteReviewsFromRemoteRepository,
            deleteReviewsFromLocalRepository,
            deleteAllCacheFromLocalRepository,
            getUserFromLocalDataSource,
            getUserFromRemoteDataSource,
            deleteUserFromAuthDataSource,
            deleteUserFromRemoteDataSource,
            deleteImageFromRemoteDataSource,
            deleteImageFromLocalDataSource,
            deleteUsersFromLocalDataSource,
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the remote repository_then the NHA image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                remoteNonHumanAnimalImageDeletedArg = false
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the local repository_then the NHA image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                localNonHumanAnimalImageDeletedArg = false
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting non human animals in the remote repository_then the data is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                databaseResultAfterDeletingAllRemoteNonHumanAnimalArg = DatabaseResult.Error()
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting non human animals in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                rowsDeletedOfAllNonHumanAnimalsArg = 0
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
    fun `given a registered user_when that user deletes their account using their password and have no reviews_then their account is deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                getRemoteReviewsResult = emptyList()
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
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their reviews from remote repository_then an error is logged`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                deleteRemoteReviewsArg = DatabaseResult.Error("error"),
            )
            deleteAccountViewmodel.deleteAccount(userPwd)

            runCurrent()

            verify {
                log.e(any(), any())
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their reviews from local repository_then an error is logged`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                deleteLocalReviewsArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)

            runCurrent()

            verify {
                log.e(any(), any())
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their cache from local repository_then an error is logged`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                deleteLocalCacheEntityArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)

            runCurrent()

            verify {
                log.e(any(), any())
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
