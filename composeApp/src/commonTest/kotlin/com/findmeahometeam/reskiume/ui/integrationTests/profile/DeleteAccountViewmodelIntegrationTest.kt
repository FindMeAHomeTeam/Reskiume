package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
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
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.nonHumanAnimal
import com.findmeahometeam.reskiume.rescueEvent
import com.findmeahometeam.reskiume.rescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.review
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeCheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeDeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeFireStoreRemoteRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalFosterHomeRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalRescueEventRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalReviewRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteNonHumanAnimalRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountViewmodel
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.DeleteNonHumanAnimalUtil
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteAccountViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private fun getDeleteAccountViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        fireStoreRemoteRescueEventRepository: FireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(),
        localRescueEventRepository: LocalRescueEventRepository = FakeLocalRescueEventRepository(),
        fireStoreRemoteFosterHomeRepository: FireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(),
        localFosterHomeRepository: LocalFosterHomeRepository = FakeLocalFosterHomeRepository(),
        deleteNonHumanAnimalUtil: DeleteNonHumanAnimalUtil = FakeDeleteNonHumanAnimalUtil(),
        checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(),
        realtimeDatabaseRemoteNonHumanAnimalRepository: RealtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(),
        localNonHumanAnimalRepository: LocalNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(),
        realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(),
        localReviewRepository: LocalReviewRepository = FakeLocalReviewRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        log: Log = FakeLog()
    ): DeleteAccountViewmodel {

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

        val deleteAllNonHumanAnimalsFromRemoteRepository =
            DeleteAllNonHumanAnimalsFromRemoteRepository(
                realtimeDatabaseRemoteNonHumanAnimalRepository
            )

        val deleteAllNonHumanAnimalsFromLocalRepository =
            DeleteAllNonHumanAnimalsFromLocalRepository(localNonHumanAnimalRepository)

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

        val log: Log = FakeLog()

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
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    mutableListOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
                ),
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    mutableListOf(fosterHomeWithAllNonHumanAnimalData)
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id + "second",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id + "second"}.webp"
                        ),
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        ),
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").imageUrl
                        ),
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        ),
                        Pair(
                            "local_path",
                            rescueEvent.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    listOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    )
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a rescue event image in the remote repository_then the rescue event image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    mutableListOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
                ),
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    mutableListOf(fosterHomeWithAllNonHumanAnimalData)
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id + "second",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id + "second"}.webp"
                        ),
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").imageUrl
                        ),
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        ),
                        Pair(
                            "local_path",
                            rescueEvent.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    listOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    )
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a rescue event image in the local repository_then the rescue event image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    mutableListOf(rescueEvent.toData())
                ),
                localRescueEventRepository = FakeLocalRescueEventRepository(
                    mutableListOf(rescueEventWithAllNeedsAndNonHumanAnimalData)
                ),
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    mutableListOf(fosterHomeWithAllNonHumanAnimalData)
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id + "second",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id + "second"}.webp"
                        ),
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        ),
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").imageUrl
                        ),
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    listOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    )
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting rescue events in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteRescueEventRepository = FakeFireStoreRemoteRescueEventRepository(
                    mutableListOf(rescueEvent.toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id + "second",
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id + "second"}.webp"
                        ),
                        Pair(
                            "${Section.RESCUE_EVENTS.path}/${user.uid}",
                            "${rescueEvent.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").imageUrl
                        ),
                        Pair(
                            "local_path",
                            rescueEvent.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toData(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toData(),
                    )
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(
                        nonHumanAnimal.toEntity(),
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second").toEntity()
                    )
                ),
                checkNonHumanAnimalUtil = FakeCheckNonHumanAnimalUtil(
                    listOf(
                        nonHumanAnimal,
                        nonHumanAnimal.copy(id = nonHumanAnimal.id + "second")
                    )
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a foster home image in the remote repository_then the foster home image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    mutableListOf(fosterHomeWithAllNonHumanAnimalData)
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a foster home image in the local repository_then the foster home image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                localFosterHomeRepository = FakeLocalFosterHomeRepository(
                    mutableListOf(fosterHomeWithAllNonHumanAnimalData)
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting foster homes in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                fireStoreRemoteFosterHomeRepository = FakeFireStoreRemoteFosterHomeRepository(
                    mutableListOf(fosterHome.toData())
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        ),
                        Pair(
                            "${Section.FOSTER_HOMES.path}/${user.uid}",
                            "${fosterHome.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        ),
                        Pair(
                            "local_path",
                            fosterHome.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the remote repository_then the NHA image is not deleted`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        ),
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting a non human animal image in the local repository_then the NHA image is not deleted in local`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(mutableListOf(review.toEntity())),
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image),
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                ),
                localNonHumanAnimalRepository = FakeLocalNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toEntity())
                )
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
    fun `given a registered user_when that user deletes their account using their password but fails deleting non human animals in the local repository_then the data is not deleted in the local repository`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                localCacheRepository = FakeLocalCacheRepository(
                    mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            cachedObjectId = nonHumanAnimal.id,
                            section = Section.NON_HUMAN_ANIMALS
                        ).toEntity()
                    )
                ),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair(
                            "${Section.NON_HUMAN_ANIMALS.path}/${user.uid}",
                            "${nonHumanAnimal.id}.webp"
                        )
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            nonHumanAnimal.imageUrl
                        )
                    )
                ),
                realtimeDatabaseRemoteNonHumanAnimalRepository = FakeRealtimeDatabaseRemoteNonHumanAnimalRepository(
                    mutableListOf(nonHumanAnimal.toData())
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
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
                localCacheRepository = FakeLocalCacheRepository(mutableListOf(localCache.toEntity())),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(user)),
                storageRepository = FakeStorageRepository(
                    remoteDatasourceList = mutableListOf(
                        Pair("${Section.USERS.path}/${user.uid}", user.image)
                    ),
                    localDatasourceList = mutableListOf(
                        Pair(
                            "local_path",
                            user.image
                        )
                    )
                )
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
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the auth repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel()
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error retrieving their account on the remote repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `given a registered user_when that user deletes their account using their password but there is an error deleting their account on local repository_then the app displays an error`() =
        runTest {
            val deleteAccountViewmodel = getDeleteAccountViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = user.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    mutableListOf(user.toData())
                )
            )
            deleteAccountViewmodel.deleteAccount(userPwd)
            deleteAccountViewmodel.deletionState.test {
                assertTrue { awaitItem() is UiState.Idle }
                assertTrue { awaitItem() is UiState.Loading }
                assertTrue { awaitItem() is UiState.Error }
                ensureAllEventsConsumed()
            }
        }
}
