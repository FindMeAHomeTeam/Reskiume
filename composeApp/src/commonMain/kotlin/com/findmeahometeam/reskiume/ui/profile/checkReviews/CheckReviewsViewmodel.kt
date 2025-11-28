package com.findmeahometeam.reskiume.ui.profile.checkReviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
    savedStateHandle: SavedStateHandle,
    private val observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    getReviewsFromLocalRepository: GetReviewsFromLocalRepository,
    private val insertReviewInLocalRepository: InsertReviewInLocalRepository,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val log: Log
) : ViewModel() {

    private val uid = savedStateHandle.toRoute<CheckReviews>().uid

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDataIfNotMine(): Flow<User?> = observeAuthStateFromAuthDataSource().flatMapConcat { authUser: AuthUser? ->
        if (authUser == null || authUser.uid == uid) {
            flowOf(null)
        } else {
            getUserFromRemoteDataSource(uid)
        }
    }

    // Flow of UiReview list to be observed by the UI
    // Decides whether to fetch from remote or local based on cache status, and updates the cache accordingly
    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    val reviewListFlow: Flow<List<UiReview>> =
        flowOf(uid).flatMapConcat { uid: String ->

            getDataByManagingObjectLocalCacheTimestamp(
                uid = uid,
                section = Section.REVIEWS,
                onCompletionInsertCache = {
                    getReviewsFromRemoteRepository(uid).insertRemoteReviewsAndMapThemToUiReview()
                },
                onCompletionUpdateCache = {
                    getReviewsFromRemoteRepository(uid).insertRemoteReviewsAndMapThemToUiReview()
                },
                onVerifyCacheIsRecent = {
                    getReviewsFromLocalRepository(uid).mapLocalReviewsToUiReview()
                }
            )
        }

    private fun Flow<List<Review>>.insertRemoteReviewsAndMapThemToUiReview(): Flow<List<UiReview>> =
        this.map { list ->
            list.map { review ->
                insertReviewInLocalRepository(review) {
                    if (it > 0) {
                        log.d(
                            "ReviewAccountViewmodel",
                            "Review ${review.timestamp} added to local database"
                        )
                    } else {
                        log.e(
                            "ReviewAccountViewmodel",
                            "Error adding review ${review.timestamp} to local database"
                        )
                    }
                }
                review.toUiReview()
            }
        }

    private fun Flow<List<Review>>.mapLocalReviewsToUiReview(): Flow<List<UiReview>> =
        this.map { list ->
            list.map { review ->
                review.toUiReview()
            }
        }

    private suspend fun Review.toUiReview(): UiReview {
        val author: User? = getReviewAuthor(authorUid)
        return UiReview(
            date = getFormattedDateFromEpochSeconds(timestamp),
            authorUid = author?.uid ?: "",
            authorName = author?.username ?: "",
            authorUri = author?.image ?: "",
            description = description,
            rating = rating
        )
    }

    private suspend fun getReviewAuthor(uid: String): User? { //TODO change user cache
        val user: User? = getUserFromLocalDataSource(uid)
        return user ?: getUserFromRemoteDataSource(uid).firstOrNull()
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
