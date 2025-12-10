package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.author
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.findmeahometeam.reskiume.data.remote.response.RemoteUser
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
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.localCache
import com.findmeahometeam.reskiume.review
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsViewmodel
import com.findmeahometeam.reskiume.uiReview
import com.findmeahometeam.reskiume.user
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
import kotlin.test.assertEquals

class CheckReviewsViewmodelTest : CoroutineTestDispatcher() {

    private val onInsertLocalCacheEntity = Capture.slot<(rowId: Long) -> Unit>()

    private val onModifyUser = Capture.slot<(rowsUpdated: Int) -> Unit>()

    private val onInsertReview = Capture.slot<(rowId: Long) -> Unit>()

    private val onInsertUserFromLocal = Capture.slot<(Long) -> Unit>()

    private val onModifyUserFromLocal = Capture.slot<(Int) -> Unit>()

    private val onSaveImageToLocal = Capture.slot<(String) -> Unit>()

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getCheckReviewsViewmodel(
        uidArg: String = user.uid,
        authStateReturn: AuthUser? = authUser,
        getUserLocalCacheEntityReturn: LocalCacheEntity? =
            localCache.copy(section = Section.USERS).toEntity(),
        getUserReviewsLocalCacheEntityReturn: LocalCacheEntity? = localCache.toEntity(),
        getAuthorLocalCacheEntityReturn: LocalCacheEntity? =
            localCache.copy(uid = author.uid, section = Section.USERS).toEntity(),
        localCacheIdInsertedInLocalDatasourceArg: Long = 1L,
        localCacheUpdatedInLocalDatasourceArg: Int = 1,
        getRemoteReviewsReturn: Flow<List<RemoteReview>> = flowOf(listOf(review.toData())),
        getLocalReviewsReturn: Flow<List<ReviewEntity>> = flowOf(listOf(review.toEntity())),
        reviewIdInsertedInLocalDatasourceArg: Long = 1L,
        rowIdInsertedUserArg: Long = 1L,
        rowsUpdatedUserArg: Int = 1,
        getRemoteUserReturn: Flow<RemoteUser?> = flowOf(user.toData()),
        getRemoteAuthorReturn: Flow<RemoteUser?> = flowOf(author.toData()),
        absolutePathUserArg: String = user.image,
        absolutePathAuthorArg: String = user.image
    ): CheckReviewsViewmodel {

        val saveStateHandleProvider: SaveStateHandleProvider = mock {
            every {
                provideObjectRoute<CheckReviews>(any(), any())
            } returns CheckReviews(uidArg)
        }

        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authStateReturn))
        }

        val localCacheRepository: LocalCacheRepository = mock {
            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.USERS
                )
            } returns getUserLocalCacheEntityReturn

            everySuspend {
                getLocalCacheEntity(
                    user.uid,
                    Section.REVIEWS
                )
            } returns getUserReviewsLocalCacheEntityReturn

            everySuspend {
                getLocalCacheEntity(
                    author.uid,
                    Section.USERS
                )
            } returns getAuthorLocalCacheEntityReturn

            everySuspend {
                insertLocalCacheEntity(
                    any(),
                    capture(onInsertLocalCacheEntity)
                )
            } calls {
                onInsertLocalCacheEntity.get().invoke(localCacheIdInsertedInLocalDatasourceArg)
            }

            everySuspend {
                modifyLocalCacheEntity(
                    any(),
                    capture(onModifyUser)
                )
            } calls { onModifyUser.get().invoke(localCacheUpdatedInLocalDatasourceArg) }
        }

        val realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = mock {
            every {
                getRemoteReviews(review.reviewedUid)
            } returns getRemoteReviewsReturn
        }

        val localReviewRepository: LocalReviewRepository = mock {
            every {
                getLocalReviews(review.reviewedUid)
            } returns getLocalReviewsReturn

            everySuspend {
                insertLocalReview(
                    review.toEntity(),
                    capture(onInsertReview)
                )
            } calls { onInsertReview.get().invoke(reviewIdInsertedInLocalDatasourceArg) }
        }

        val localUserRepository: LocalUserRepository = mock {
            everySuspend {
                getUser(author.uid)
            } returns author

            everySuspend { insertUser(any(), capture(onInsertUserFromLocal)) } calls {
                onInsertUserFromLocal.get().invoke(rowIdInsertedUserArg)
            }
            everySuspend { modifyUser(any(), capture(onModifyUserFromLocal)) } calls {
                onModifyUserFromLocal.get().invoke(rowsUpdatedUserArg)
            }
        }

        val realtimeDatabaseRemoteUserRepository: RealtimeDatabaseRemoteUserRepository = mock {
            every {
                getRemoteUser(user.uid)
            } returns getRemoteUserReturn

            every {
                getRemoteUser(author.uid)
            } returns getRemoteAuthorReturn
        }

        val storageRepository: StorageRepository = mock {
            every {
                downloadImage(
                    user.uid,
                    "",
                    Section.USERS,
                    capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathUserArg) }

            every {
                downloadImage(
                    author.uid,
                    "",
                    Section.USERS,
                    capture(onSaveImageToLocal)
                )
            } calls { onSaveImageToLocal.get().invoke(absolutePathAuthorArg) }
        }

        val observeAuthStateInAuthDataSource =
            ObserveAuthStateInAuthDataSource(authRepository)

        val getDataByManagingObjectLocalCacheTimestamp =
            GetDataByManagingObjectLocalCacheTimestamp(localCacheRepository, log)

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
            insertUserInLocalDataSource,
            modifyUserInLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when the user opens their reviews section_then their profile is not displayed`() =
        runTest {
            getCheckReviewsViewmodel().getUserDataIfNotMine().test {
                assertEquals(null, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a user with empty cache_when the user clicks on a review_then the reviewed user profile is saved in local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                uidArg = author.uid,
                authStateReturn = null,
                getAuthorLocalCacheEntityReturn = null
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with empty cache and no avatar_when the user clicks on a review but have an error saving the reviewed user locally_then the reviewed user profile is displayed but not saved in local cache`() =
        runTest {
            getCheckReviewsViewmodel(
                uidArg = author.uid,
                authStateReturn = null,
                getAuthorLocalCacheEntityReturn = null,
                getRemoteAuthorReturn = flowOf(author.copy(image = "").toData()),
                rowIdInsertedUserArg = 0,
                absolutePathAuthorArg = ""
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(image = "", savedBy = "", email = null), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.e(
                    "CheckReviewsViewmodel",
                    "Error adding user ${author.uid} to local database"
                )
            }
        }

    @Test
    fun `given a user with an old local cache_when the user clicks on a review_then the reviewed user profile is modified in local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                uidArg = author.uid,
                authStateReturn = null,
                getAuthorLocalCacheEntityReturn =
                localCache.copy(uid = author.uid, section = Section.USERS, timestamp = 123L).toEntity()
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(savedBy = "", email = null), awaitItem())
                awaitComplete()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a user with an outdated local cache and no avatar_when the user clicks on a review but there is an error modifying the retrieved user locally_then the reviewed user is displayed but not modified`() =
        runTest {
            getCheckReviewsViewmodel(
                uidArg = author.uid,
                authStateReturn = null,
                getAuthorLocalCacheEntityReturn =
                    localCache.copy(uid = author.uid, section = Section.USERS, timestamp = 123L).toEntity(),
                getRemoteAuthorReturn = flowOf(author.copy(image = "").toData()),
                rowsUpdatedUserArg = 0,
                absolutePathAuthorArg = ""
            ).getUserDataIfNotMine().test {
                assertEquals(author.copy(image = "", savedBy = "", email = null), awaitItem())
                awaitComplete()
            }

            runCurrent()

            verify {
                log.e(
                    "CheckReviewsViewmodel",
                    "Failed to modify user with uid ${author.uid} in local data source."
                )
            }
        }

    @Test
    fun `given a user with recent local cache_when the user clicks on a review_then the reviewed user profile is retrieved from local cache and displayed`() =
        runTest {
            getCheckReviewsViewmodel(
                uidArg = author.uid,
                authStateReturn = null,
            ).getUserDataIfNotMine().test {
                assertEquals(author, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user with an empty cache_when the user opens the reviews section_then that user will see the review section populated`() =
        runTest {
            getCheckReviewsViewmodel(
                getUserLocalCacheEntityReturn = null,
                getUserReviewsLocalCacheEntityReturn = null,
                getAuthorLocalCacheEntityReturn = null
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given a registered user with an outdated cache_when the user opens the reviews section but there is an error inserting reviews in local repository_then the review section is populated but not updated`() =
        runTest {
            getCheckReviewsViewmodel(
                getUserReviewsLocalCacheEntityReturn = localCache.copy(timestamp = 123L).toEntity(),
                reviewIdInsertedInLocalDatasourceArg = 0
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

            runCurrent()

            verify {
                log.e(
                    "CheckReviewsViewmodel",
                    "Error adding review ${review.timestamp} to local database"
                )
            }
        }

    @Test
    fun `given a registered user with recent cache_when the user opens the reviews section_then the user will see them`() =
        runTest {
            getCheckReviewsViewmodel().reviewListFlow.test {
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
