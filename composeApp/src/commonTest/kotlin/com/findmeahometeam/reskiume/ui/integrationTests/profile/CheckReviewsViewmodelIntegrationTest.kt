package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.author
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteUser.RealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.storage.StorageRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.review
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeKonnectivity
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalCacheRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalReviewRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeRealtimeDatabaseRemoteUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeSaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeStorageRepository
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.uiReview
import com.findmeahometeam.reskiume.userPwd
import com.plusmobileapps.konnectivity.Konnectivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckReviewsViewmodelIntegrationTest : CoroutineTestDispatcher() {


    private fun getCheckReviewsViewmodel(
        saveStateHandleProvider: SaveStateHandleProvider = FakeSaveStateHandleProvider(),
        authRepository: AuthRepository = FakeAuthRepository(),
        localCacheRepository: LocalCacheRepository = FakeLocalCacheRepository(),
        log: Log = FakeLog(),
        realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(),
        localReviewRepository: LocalReviewRepository = FakeLocalReviewRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository(),
        realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(),
        storageRepository: StorageRepository = FakeStorageRepository(),
        konnectivity: Konnectivity = FakeKonnectivity(),
        manageImagePath: ManageImagePath = FakeManageImagePath()
    ): CheckReviewsViewmodel {

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log, konnectivity)

        val getReviewsFromRemoteRepository =
            GetReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

        val getReviewsFromLocalRepository =
            GetReviewsFromLocalRepository(localReviewRepository)

        val insertReviewInLocalRepository =
            InsertReviewInLocalRepository(localReviewRepository, authRepository)

        val getUserFromLocalDataSource =
            GetUserFromLocalDataSource(localUserRepository)

        val getUserFromRemoteDataSource =
            GetUserFromRemoteDataSource(realtimeDatabaseRemoteUserRepository)

        val downloadImageToLocalDataSource =
            DownloadImageToLocalDataSource(storageRepository)

        val insertUserInLocalDataSource =
            InsertUserInLocalDataSource(localUserRepository, authRepository)

        val modifyUserInLocalDataSource =
            ModifyUserInLocalDataSource(localUserRepository, authRepository)

        val getCompleteImagePathFromLocalDataSource =
            GetCompleteImagePathFromLocalDataSource(manageImagePath)

        return CheckReviewsViewmodel(
            saveStateHandleProvider,
            observeAuthStateInAuthDataSource,
            getDataByManagingObjectLocalCacheTimestamp,
            getReviewsFromRemoteRepository,
            getReviewsFromLocalRepository,
            insertReviewInLocalRepository,
            getUserFromLocalDataSource,
            getUserFromRemoteDataSource,
            downloadImageToLocalDataSource,
            getCompleteImagePathFromLocalDataSource,
            insertUserInLocalDataSource,
            modifyUserInLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when the user opens their reviews section_then their profile is not displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                authRepository = FakeAuthRepository(authUser = authUser)
            ).getUserDataIfNotMine().test {
                assertEquals(null, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with empty cache_when the user clicks on a review_then the reviewed user profile is saved in local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(CheckReviews(author.uid)),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    remoteUserList = mutableListOf(author.toData())
                )
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with empty cache and no avatar_when the user clicks on a review but have an error saving the reviewed user locally_then the reviewed user profile is displayed but not saved in local cache`() =
        runTest {
            getCheckReviewsViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(CheckReviews(author.uid)),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    remoteUserList = mutableListOf(author.copy(image = "").toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(author.copy(image = "")))
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(image = "", savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with an old local cache_when the user clicks on a review_then the reviewed user profile is modified in local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(CheckReviews(author.uid)),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    remoteUserList = mutableListOf(author.toData())
                ),
                localUserRepository = FakeLocalUserRepository(mutableListOf(author)),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with an outdated local cache and no avatar_when the user clicks on a review but there is an error modifying the retrieved user locally_then the reviewed user is displayed but not modified`() =
        runTest {
            getCheckReviewsViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(CheckReviews(author.uid)),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteUserRepository = FakeRealtimeDatabaseRemoteUserRepository(
                    remoteUserList = mutableListOf(author.copy(image = "").toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS,
                            timestamp = 123L
                        ).toEntity()
                    )
                )
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(image = "", savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with recent local cache_when the user clicks on a review_then the reviewed user profile is retrieved from local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                saveStateHandleProvider = FakeSaveStateHandleProvider(CheckReviews(author.uid)),
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                localUserRepository = FakeLocalUserRepository(
                    localUserList = mutableListOf(author)
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS
                        ).toEntity()
                    )
                )
            ).getUserDataIfNotMine().test {
                assertEquals(author, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user with an empty cache_when the user opens the reviews section_then that user will see the review section populated`() =
        runTest {
            getCheckReviewsViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    remoteReviews = mutableListOf(review.toData())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            section = Section.USERS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS
                        ).toEntity()
                    )
                ),
                localUserRepository = FakeLocalUserRepository(
                    localUserList = mutableListOf(author)
                )
            ).reviewListFlow.test {
                val actualUiReviewList = awaitItem()

                assertEquals(uiReview.date, actualUiReviewList[0].date)
                assertEquals(uiReview.authorUid, actualUiReviewList[0].authorUid)
                assertEquals(uiReview.authorName, actualUiReviewList[0].authorName)
                assertEquals(uiReview.authorUri, actualUiReviewList[0].authorUri)
                assertEquals(uiReview.description, actualUiReviewList[0].description)
                assertEquals(uiReview.rating, actualUiReviewList[0].rating)

                awaitComplete()
            }
        }

    @Test
    fun `given a registered user with an outdated cache_when the user opens the reviews section but there is an error inserting reviews in local repository_then the review section is populated but not updated`() =
        runTest {
            getCheckReviewsViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                realtimeDatabaseRemoteReviewRepository = FakeRealtimeDatabaseRemoteReviewRepository(
                    remoteReviews = mutableListOf(review.toData())
                ),
                localReviewRepository = FakeLocalReviewRepository(
                    reviews = mutableListOf(review.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.copy(
                            section = Section.USERS,
                            timestamp = 123L
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS
                        ).toEntity()
                    )
                ),
                localUserRepository = FakeLocalUserRepository(
                    localUserList = mutableListOf(author)
                )
            ).reviewListFlow.test {
                val actualUiReviewList = awaitItem()

                assertEquals(uiReview.date, actualUiReviewList[0].date)
                assertEquals(uiReview.authorUid, actualUiReviewList[0].authorUid)
                assertEquals(uiReview.authorName, actualUiReviewList[0].authorName)
                assertEquals(uiReview.authorUri, actualUiReviewList[0].authorUri)
                assertEquals(uiReview.description, actualUiReviewList[0].description)
                assertEquals(uiReview.rating, actualUiReviewList[0].rating)

                awaitComplete()
            }
        }

    @Test
    fun `given a registered user with recent cache_when the user opens the reviews section_then the user will see them`() =
        runTest {
            getCheckReviewsViewmodel(
                authRepository = FakeAuthRepository(
                    authUser = authUser,
                    authEmail = authUser.email,
                    authPassword = userPwd
                ),
                localReviewRepository = FakeLocalReviewRepository(
                    reviews = mutableListOf(review.toEntity())
                ),
                localCacheRepository = FakeLocalCacheRepository(
                    localCacheList = mutableListOf(
                        localCache.toEntity(),
                        localCache.copy(
                            section = Section.USERS
                        ).toEntity(),
                        localCache.copy(
                            cachedObjectId = author.uid,
                            section = Section.USERS
                        ).toEntity()
                    )
                ),
                localUserRepository = FakeLocalUserRepository(
                    localUserList = mutableListOf(author)
                )
            ).reviewListFlow.test {
                val actualUiReviewList = awaitItem()

                assertEquals(uiReview.date, actualUiReviewList[0].date)
                assertEquals(uiReview.authorUid, actualUiReviewList[0].authorUid)
                assertEquals(uiReview.authorName, actualUiReviewList[0].authorName)
                assertEquals(uiReview.authorUri, actualUiReviewList[0].authorUri)
                assertEquals(uiReview.description, actualUiReviewList[0].description)
                assertEquals(uiReview.rating, actualUiReviewList[0].rating)

                awaitComplete()
            }
        }
}
