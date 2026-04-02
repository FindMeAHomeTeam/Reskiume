package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteNonHumanAnimal.RealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteFosterHome.FireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.remoteRescueEvent.FireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetAllUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.fosterHome
import com.findmeahometeam.reskiume.fosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.review
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
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

    private val onDeleteAllMyRescueEventsFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteAllMyRescueEventsFromLocal = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteAllMyFosterHomesFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteAllMyFosterHomesFromLocal = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onModifyNonHumanAnimalFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifySecondNonHumanAnimalFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onModifyNonHumanAnimalInLocal = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onModifySecondNonHumanAnimalInLocal = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onDeleteAllNonHumanAnimalFromRemote = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteAllNonHumanAnimalFromLocal = Capture.slot<(rowsDeleted: Int) -> Unit>()

    private val onDeleteRemoteReviews = Capture.slot<(DatabaseResult) -> Unit>()

    private val onDeleteLocalReviews = Capture.slot<(Int) -> Unit>()

    private val onDeleteAllLocalCacheEntity = Capture.slot<(Int) -> Unit>()

    private val onSuccessDeleteRemoteUser = Capture.slot<(DatabaseResult) -> Unit>()

    private val onRemoteImageDeleted = Capture.slot<(Boolean) -> Unit>()

    private val onNonHumanAnimalImageDeletedFromRemote =
        Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onFosterHomeImageDeletedFromRemote = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onRescueEventImageDeletedFromRemote = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onNonHumanAnimalImageDeletedFromLocal = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onFosterHomeImageDeletedFromLocal = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onRescueEventImageDeletedFromLocal = Capture.slot<(isDeleted: Boolean) -> Unit>()

    private val onUserImageDeletedFromLocal = Capture.slot<(Boolean) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getDeleteAccountViewmodel(
        authStateResult: AuthUser? = authUser,
        databaseResultOfDeletingAllRemoteRescueEventsArg: DatabaseResult = DatabaseResult.Success,
        rowsDeletedOfAllMyRescueEventsArg: Int = 1,
        deleteUserFromAuthErrorArg: String = "",
        myRemoteFosterHomesResult: List<RemoteFosterHome?> = listOf(fosterHome.toData()),
        databaseResultOfDeletingAllRemoteFosterHomesArg: DatabaseResult = DatabaseResult.Success,
        localFosterHomesResult: List<FosterHomeWithAllNonHumanAnimalData> = listOf(
            fosterHomeWithAllNonHumanAnimalData
        ),
        rowsDeletedOfAllMyFosterHomesArg: Int = 1,
        databaseResultOfModifyingNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        databaseResultOfModifyingSecondNonHumanAnimalArg: DatabaseResult = DatabaseResult.Success,
        rowsUpdatedOfModifyingNonHumanAnimalsArg: Int = 1,
        rowsUpdatedOfModifyingSecondNonHumanAnimalsArg: Int = 1,
        getUserResult: User = user,
        getAllUsersResult: List<UserEntity> = listOf(user.toEntity()),
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
        remoteFosterHomeImageDeletedArg: Boolean = true,
        flagOfDeletingRemoteRescueEventImageArg: Boolean = true,
        localImageDeletedArg: Boolean = true,
        localNonHumanAnimalImageDeletedArg: Boolean = true,
        localFosterHomeImageDeletedArg: Boolean = true,
        flagOfDeletingLocalRescueEventImageArg: Boolean = true
    ): DeleteAccountViewmodel {

        val authRepository: AuthRepository = mock {

            every { authState } returns flowOf(authStateResult)

            everySuspend { deleteUser(any(), capture(onDeleteUserFromAuth)) } calls {
                onDeleteUserFromAuth.get().invoke(deleteUserFromAuthErrorArg)
            }
        }

        val fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = mock {

            every {
                getAllMyRemoteRescueEvents(
                    rescueEvent.creatorId
                )
            } returns flowOf(listOf(rescueEvent.toData()))

            everySuspend {
                deleteAllMyRemoteRescueEvents(
                    rescueEvent.creatorId,
                    capture(onDeleteAllMyRescueEventsFromRemote)
                )
            } calls {
                onDeleteAllMyRescueEventsFromRemote.get().invoke(databaseResultOfDeletingAllRemoteRescueEventsArg)
            }
        }

        val localRescueEventRepository: LocalRescueEventRepository = mock {

            every {
                getAllRescueEvents()
            } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData))

            every {
                getAllMyRescueEvents(rescueEvent.creatorId)
            } returns flowOf(listOf(rescueEventWithAllNeedsAndNonHumanAnimalData))

            everySuspend {
                deleteAllMyRescueEvents(
                    rescueEvent.creatorId,
                    capture(onDeleteAllMyRescueEventsFromLocal)
                )
            } calls {
                onDeleteAllMyRescueEventsFromLocal.get().invoke(rowsDeletedOfAllMyRescueEventsArg)
            }
        }

        val fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = mock {

            everySuspend {
                getAllMyRemoteFosterHomes(
                    fosterHome.ownerId
                )
            } returns flowOf(myRemoteFosterHomesResult)

            everySuspend {
                deleteAllMyRemoteFosterHomes(
                    fosterHome.ownerId,
                    capture(onDeleteAllMyFosterHomesFromRemote)
                )
            } calls {
                onDeleteAllMyFosterHomesFromRemote.get()
                    .invoke(databaseResultOfDeletingAllRemoteFosterHomesArg)
            }
        }

        val deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = mock {

            everySuspend {
                deleteNonHumanAnimal(
                    id = nonHumanAnimal.id,
                    caregiverId = nonHumanAnimal.caregiverId,
                    coroutineScope = any(),
                    onlyDeleteOnLocal = false,
                    onError = any(),
                    onComplete = any()
                )
            }
        }

        val localFosterHomeRepository: LocalFosterHomeRepository = mock {

            every {
                getAllFosterHomes()
            } returns flowOf(localFosterHomesResult)

            every {
                getAllMyFosterHomes(fosterHome.ownerId)
            } returns flowOf(localFosterHomesResult)

            everySuspend {
                deleteAllMyFosterHomes(
                    fosterHome.ownerId,
                    capture(onDeleteAllMyFosterHomesFromLocal)
                )
            } calls {
                onDeleteAllMyFosterHomesFromLocal.get().invoke(rowsDeletedOfAllMyFosterHomesArg)
            }
        }

        val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = mock {

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal)

            every {
                getNonHumanAnimalFlow(
                    nonHumanAnimal.id + "second",
                    nonHumanAnimal.caregiverId,
                    any()
                )
            } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second"))
        }


        val localUserRepository: LocalUserRepository = mock {

            everySuspend { getUser(user.uid) } returns getUserResult

            everySuspend { getAllUsers() } returns getAllUsersResult

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
                    capture(onNonHumanAnimalImageDeletedFromRemote)
                )
            } calls {
                onNonHumanAnimalImageDeletedFromRemote.get()
                    .invoke(remoteNonHumanAnimalImageDeletedArg)
            }

            everySuspend {
                deleteRemoteImage(
                    fosterHome.ownerId,
                    fosterHome.id,
                    Section.FOSTER_HOMES,
                    capture(onFosterHomeImageDeletedFromRemote)
                )
            } calls {
                onFosterHomeImageDeletedFromRemote.get().invoke(remoteFosterHomeImageDeletedArg)
            }

            everySuspend {
                deleteRemoteImage(
                    rescueEvent.creatorId,
                    rescueEvent.id,
                    Section.RESCUE_EVENTS,
                    capture(onRescueEventImageDeletedFromRemote)
                )
            } calls {
                onRescueEventImageDeletedFromRemote.get().invoke(flagOfDeletingRemoteRescueEventImageArg)
            }

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
            } calls {
                onNonHumanAnimalImageDeletedFromLocal.get()
                    .invoke(localNonHumanAnimalImageDeletedArg)
            }

            every {
                deleteLocalImage(
                    fosterHome.imageUrl,
                    capture(onFosterHomeImageDeletedFromLocal)
                )
            } calls {
                onFosterHomeImageDeletedFromLocal.get()
                    .invoke(localFosterHomeImageDeletedArg)
            }

            every {
                deleteLocalImage(
                    rescueEvent.imageUrl,
                    capture(onRescueEventImageDeletedFromLocal)
                )
            } calls {
                onRescueEventImageDeletedFromLocal.get()
                    .invoke(flagOfDeletingLocalRescueEventImageArg)
            }
        }

        val realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository =
            mock {

                every {
                    getAllRemoteNonHumanAnimals(nonHumanAnimal.caregiverId)
                } returns flowOf(listOf(nonHumanAnimal.toData()))

                every {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id, nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.toData())

                every {
                    getRemoteNonHumanAnimal(nonHumanAnimal.id + "second", nonHumanAnimal.caregiverId)
                } returns flowOf(nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData())

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.toData(),
                        capture(onModifyNonHumanAnimalFromRemote)
                    )
                } calls {
                    onModifyNonHumanAnimalFromRemote.get()
                        .invoke(databaseResultOfModifyingNonHumanAnimalArg)
                }

                everySuspend {
                    modifyRemoteNonHumanAnimal(
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                        capture(onModifySecondNonHumanAnimalFromRemote)
                    )
                } calls {
                    onModifySecondNonHumanAnimalFromRemote.get()
                        .invoke(databaseResultOfModifyingSecondNonHumanAnimalArg)
                }

                every {
                    deleteAllRemoteNonHumanAnimals(
                        nonHumanAnimal.caregiverId,
                        capture(onDeleteAllNonHumanAnimalFromRemote)
                    )
                } calls {
                    onDeleteAllNonHumanAnimalFromRemote.get()
                        .invoke(databaseResultAfterDeletingAllRemoteNonHumanAnimalArg)
                }
            }

        val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = mock {

            every {
                getAllNonHumanAnimals()
            } returns flowOf(listOf(nonHumanAnimal.toEntity()))

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.toEntity(),
                    capture(onModifyNonHumanAnimalInLocal)
                )
            } calls {
                onModifyNonHumanAnimalInLocal.get().invoke(rowsUpdatedOfModifyingNonHumanAnimalsArg)
            }

            everySuspend {
                modifyNonHumanAnimal(
                    nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity(),
                    capture(onModifySecondNonHumanAnimalInLocal)
                )
            } calls {
                onModifySecondNonHumanAnimalInLocal.get().invoke(rowsUpdatedOfModifyingSecondNonHumanAnimalsArg)
            }

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

        val getAllMyRescueEventsFromRemoteRepository =
            GetAllMyRescueEventsFromRemoteRepository(fireStoreRemoteRescueEventRepository)

        val getAllRescueEventsFromLocalRepository =
            GetAllRescueEventsFromLocalRepository(localRescueEventRepository)

        val deleteAllMyRescueEventsFromRemoteRepository =
            DeleteAllMyRescueEventsFromRemoteRepository(
                authRepository,
                fireStoreRemoteRescueEventRepository,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                deleteNonHumanAnimalUtil,
                log
            )

        val deleteAllMyRescueEventsFromLocalRepository =
            DeleteAllMyRescueEventsFromLocalRepository(
                localRescueEventRepository,
                checkNonHumanAnimalUtil,
                localNonHumanAnimalRepository,
                log
            )

        val getAllMyFosterHomesFromRemoteRepository =
            GetAllMyFosterHomesFromRemoteRepository(fireStoreRemoteFosterHomeRepository)

        val getAllFosterHomesFromLocalRepository =
            GetAllFosterHomesFromLocalRepository(localFosterHomeRepository)

        val deleteAllMyFosterHomesFromRemoteRepository =
            DeleteAllMyFosterHomesFromRemoteRepository(
                authRepository,
                fireStoreRemoteFosterHomeRepository,
                deleteNonHumanAnimalUtil,
                realtimeDatabaseRemoteNonHumanAnimalRepository,
                log
            )

        val deleteAllMyFosterHomesFromLocalRepository =
            DeleteAllMyFosterHomesFromLocalRepository(
                localFosterHomeRepository,
                checkNonHumanAnimalUtil,
                localNonHumanAnimalRepository,
                log
            )

        val getAllNonHumanAnimalsFromRemoteRepository =
            GetAllNonHumanAnimalsFromRemoteRepository(realtimeDatabaseRemoteNonHumanAnimalRepository)

        val getReviewsFromRemoteRepository =
            GetReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

        val deleteReviewsFromRemoteRepository =
            DeleteReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

        val deleteReviewsFromLocalRepository =
            DeleteReviewsFromLocalRepository(localReviewRepository)

        val deleteAllCacheFromLocalRepository =
            DeleteAllCacheFromLocalRepository(localCacheRepository)

        val getAllUsersFromLocalDataSource =
            GetAllUsersFromLocalDataSource(localUserRepository)

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

        val getAllNonHumanAnimalsFromLocalRepository =
            GetAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        val deleteAllNonHumanAnimalsFromRemoteRepository =
            DeleteAllNonHumanAnimalsFromRemoteRepository(
                realtimeDatabaseRemoteNonHumanAnimalRepository
            )

        val deleteAllNonHumanAnimalsFromLocalRepository =
            DeleteAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

        return DeleteAccountViewmodel(
            observeAuthStateInAuthDataSource,
            getAllMyRescueEventsFromRemoteRepository,
            getAllRescueEventsFromLocalRepository,
            deleteAllMyRescueEventsFromRemoteRepository,
            deleteAllMyRescueEventsFromLocalRepository,
            getAllMyFosterHomesFromRemoteRepository,
            getAllFosterHomesFromLocalRepository,
            deleteAllMyFosterHomesFromRemoteRepository,
            deleteAllMyFosterHomesFromLocalRepository,
            getAllNonHumanAnimalsFromRemoteRepository,
            getAllNonHumanAnimalsFromLocalRepository,
            deleteAllNonHumanAnimalsFromRemoteRepository,
            deleteAllNonHumanAnimalsFromLocalRepository,
            getReviewsFromRemoteRepository,
            deleteReviewsFromRemoteRepository,
            deleteReviewsFromLocalRepository,
            deleteAllCacheFromLocalRepository,
            getAllUsersFromLocalDataSource,
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
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.d(
                    "DeleteAccountViewmodel",
                    "deleteMyUserFromLocalDataSource: User ${user.uid} deleted successfully from the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a rescue event image in the remote repository_then the rescue event image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                flagOfDeletingRemoteRescueEventImageArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllMyRescueEventImagesFromRemoteDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a rescue event image in the local repository_then the rescue event image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                flagOfDeletingLocalRescueEventImageArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllRescueEventImagesFromLocalDataSource: failed to delete the image from the rescue event ${rescueEvent.id} in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting rescue events in the remote repository_then the data is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                databaseResultOfDeletingAllRemoteRescueEventsArg = DatabaseResult.Error()
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllMyRescueEventsFromRemoteDataSource: failed to delete rescue events from the owner id ${rescueEvent.creatorId} from the remote repository: "
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting rescue events in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                rowsDeletedOfAllMyRescueEventsArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllMyRescueEventsFromLocalDataSource: failed to delete rescue events from the creator ${rescueEvent.creatorId} from the local repository"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a foster home image in the remote repository_then the foster home image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                remoteFosterHomeImageDeletedArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllMyFosterHomeImagesFromRemoteDataSource: failed to delete the image from the foster home ${fosterHome.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a foster home image in the local repository_then the foster home image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                localFosterHomeImageDeletedArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllFosterHomeImagesFromLocalDataSource: failed to delete the image from the foster home ${fosterHome.id} in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting foster homes in the remote repository_then the data is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                databaseResultOfDeletingAllRemoteFosterHomesArg = DatabaseResult.Error()
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllMyFosterHomesFromRemoteDataSource: failed to delete foster homes from the owner id ${fosterHome.ownerId} from the remote repository: "
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting foster homes in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                rowsDeletedOfAllMyFosterHomesArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllMyFosterHomesFromLocalDataSource: failed to delete foster homes from the owner ${fosterHome.ownerId} from the local repository"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the remote repository_then the NHA image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                remoteNonHumanAnimalImageDeletedArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllNonHumanAnimalImagesFromRemoteDataSource: failed to delete the image from the non human animal ${nonHumanAnimal.id} in the remote data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the local repository_then the NHA image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                localNonHumanAnimalImageDeletedArg = false
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllNonHumanAnimalImagesFromLocalDataSource: failed to delete the image from the non human animal ${nonHumanAnimal.id} in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting non human animals in the remote repository_then the data is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                databaseResultAfterDeletingAllRemoteNonHumanAnimalArg = DatabaseResult.Error()
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllNonHumanAnimalsFromRemoteDataSource: failed to delete non human animals from caregiver ${nonHumanAnimal.caregiverId} from remote repository: "
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting non human animals in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                rowsDeletedOfAllNonHumanAnimalsArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteAllNonHumanAnimalsFromLocalDataSource: failed to delete non human animals from caregiver ${nonHumanAnimal.caregiverId} from local repository"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password and have no reviews_then their account is deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                getRemoteReviewsResult = emptyList()
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
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
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteUserReviewsFromRemoteRepository: Error deleting reviews for the user ${user.uid} from the remote repository: error"
                )
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

            log.e(
                "DeleteAccountViewmodel",
                "deleteUserReviewsFromLocalRepository: No reviews to delete for the user ${user.uid} from the local repository"
            )
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their cache from local repository_then an error is logged`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                deleteLocalCacheEntityArg = 0
            )
            deleteAccountViewmodel.deleteAccount(userPwd)

            runCurrent()

            log.e(
                "DeleteAccountViewmodel",
                "deleteUserCacheFromLocalRepository: Error deleting entries for ${user.uid} from local repository"
            )
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the auth repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authStateResult = null
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "getUserFromRemoteRepo: User UID is blank"
                )
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
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Success }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAvatarFromRemoteDataSource: failed to delete the image from the user ${user.uid} in the remote data source"
                )
                log.e(
                    "DeleteAccountViewModel",
                    "deleteAllUserAvatarsFromLocalDataSource: failed to delete the image from the user ${user.uid} in the local data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the remote repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                remoteUserResult = null
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "getUserFromRemoteRepo: User ${user.uid} not found in remote data source"
                )
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their account on remote repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                successRemoteUserArg = DatabaseResult.Error("error")
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteUserFromRemoteRepository: Error deleting the user ${user.uid} from the remote repository: error"
                )
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
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
            verify {
                log.e(
                    "DeleteAccountViewmodel",
                    "deleteMyUserFromLocalDataSource: Error deleting the user ${user.uid} from the local data source"
                )
            }
        }
}
