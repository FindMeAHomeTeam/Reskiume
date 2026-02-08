package com.findmeahometeam.reskiume.ui.profile.checkReviews

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CheckReviewsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    getReviewsFromLocalRepository: GetReviewsFromLocalRepository,
    private val insertReviewInLocalRepository: InsertReviewInLocalRepository,
    private val checkActivistUtil: CheckActivistUtil,
    private val log: Log
) : ViewModel() {

    private val uid = saveStateHandleProvider.provideObjectRoute(CheckReviews::class).uid

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDataIfNotMine(): Flow<User?> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            if (authUser?.uid == uid) {
                flowOf(null)
            } else {
                flowOf(getActivist(uid, authUser?.uid ?: ""))
            }
        }

    // Flow of UiReview list to be observed by the UI
    // Decides whether to fetch from remote or local based on cache status
    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    val reviewListFlow: Flow<List<UiReview>> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = uid,
                savedBy = authUser?.uid ?: "",
                section = Section.REVIEWS,
                onCompletionInsertCache = {
                    getReviewsFromRemoteRepository(uid).insertRemoteReviewsInLocalRepositoryAndMapThemToUiReview(
                        authUser?.uid ?: ""
                    )
                },
                onCompletionUpdateCache = {
                    getReviewsFromRemoteRepository(uid).insertRemoteReviewsInLocalRepositoryAndMapThemToUiReview(
                        authUser?.uid ?: ""
                    )
                },
                onVerifyCacheIsRecent = {
                    getReviewsFromLocalRepository(uid).mapLocalReviewsToUiReview(
                        authUser?.uid ?: ""
                    )
                }
            )
        }

    private fun Flow<List<Review>>.insertRemoteReviewsInLocalRepositoryAndMapThemToUiReview(
        authUserUid: String
    ): Flow<List<UiReview>> =
        this.map { list ->
            list.map { review ->
                insertReviewInLocalRepository(review) {
                    if (it > 0) {
                        log.d(
                            "CheckReviewsViewmodel",
                            "Review ${review.timestamp} added to local database"
                        )
                    } else {
                        log.e(
                            "CheckReviewsViewmodel",
                            "Error adding review ${review.timestamp} to local database"
                        )
                    }
                }
                review.toUiReview(authUserUid)
            }
        }

    private fun Flow<List<Review>>.mapLocalReviewsToUiReview(authUserUid: String): Flow<List<UiReview>> =
        this.map { list ->
            list.map { review ->
                review.toUiReview(authUserUid)
            }
        }

    private suspend fun Review.toUiReview(myUserUid: String): UiReview {
        val author: User? = getActivist(authorUid, myUserUid)
        return UiReview(
            date = getFormattedDateFromEpochSeconds(timestamp),
            authorUid = author?.uid ?: "",
            authorName = author?.username ?: "",
            authorUri = author?.image ?: "",
            description = description,
            rating = rating
        )
    }

    private suspend fun getActivist(activistUid: String, myUserUid: String): User? {

        return checkActivistUtil.getUser(activistUid, myUserUid)
    }

    @OptIn(ExperimentalTime::class)
    private fun getFormattedDateFromEpochSeconds(epochSeconds: Long): String {
        val epoch = Instant.fromEpochSeconds(epochSeconds)
        val customFormat: DateTimeFormat<DateTimeComponents> = DateTimeComponents.Format {
            year(); char('-'); monthNumber(); char('-'); day()
        }
        return epoch.format(customFormat)
    }
}

class UiReview(
    val date: String,
    val authorUid: String,
    val authorName: String,
    val authorUri: String,
    val description: String,
    val rating: Float
)
