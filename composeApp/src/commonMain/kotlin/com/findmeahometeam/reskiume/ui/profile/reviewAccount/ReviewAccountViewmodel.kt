package com.findmeahometeam.reskiume.ui.profile.reviewAccount

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.IsCachedObjectNullOrOlderThan24H
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class ReviewAccountViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    isCachedObjectNullOrOlderThan24H: IsCachedObjectNullOrOlderThan24H,
    insertCacheInLocalRepository: InsertCacheInLocalRepository,
    modifyCacheInLocalRepository: ModifyCacheInLocalRepository,
    getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository,
    private val getReviewsFromLocalRepository: GetReviewsFromLocalRepository,
    private val insertReviewInLocalRepository: InsertReviewInLocalRepository,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    val log: Log
) : ViewModel() {

    // Flow of UiReview list to be observed by the UI
    // Decides whether to fetch from remote or local based on cache status, and updates the cache accordingly
    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    val reviewListFlow: Flow<List<UiReview>> =
        observeAuthStateFromAuthDataSource().flatMapConcat { authUser: AuthUser? ->
            val isnullOrOlder: Boolean? =
                isCachedObjectNullOrOlderThan24H(authUser!!.uid, Section.REVIEWS)
            when (isnullOrOlder) {
                null -> {
                    insertCacheInLocalRepository(
                        LocalCache(
                            uid = authUser.uid,
                            section = Section.REVIEWS,
                            timestamp = Clock.System.now().epochSeconds
                        )
                    ) { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "ReviewAccountViewmodel",
                                "${authUser.uid} added to local cache in section ${Section.REVIEWS}"
                            )
                        } else {
                            log.e(
                                "ReviewAccountViewmodel",
                                "Error adding ${authUser.uid} to local cache in section ${Section.REVIEWS}"
                            )
                        }
                    }
                    getReviewsFromRemoteRepository(authUser.uid).insertRemoteReviewsAndMapThemToUiReview()
                }

                true -> {
                    modifyCacheInLocalRepository(
                        LocalCache(
                            uid = authUser.uid,
                            section = Section.REVIEWS,
                            timestamp = Clock.System.now().epochSeconds
                        )
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "ReviewAccountViewmodel",
                                "${authUser.uid} updated in local cache in section ${Section.REVIEWS}"
                            )
                        } else {
                            log.e(
                                "ReviewAccountViewmodel",
                                "Error updating ${authUser.uid} in local cache in section ${Section.REVIEWS}"
                            )
                        }
                    }
                    getReviewsFromRemoteRepository(authUser.uid).insertRemoteReviewsAndMapThemToUiReview()
                }

                false -> {
                    getReviewsFromLocalRepository(authUser.uid).mapLocalReviewsToUiReview()
                }
            }
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
                val user: User? = getReviewAuthor(review.authorUid)
                UiReview(
                    date = getFormattedDateFromEpochSeconds(review.timestamp),
                    authorName = user?.username ?: "",
                    authorUri = user?.image ?: "",
                    description = review.description,
                    rating = review.rating
                )
            }
        }

    private fun Flow<List<Review>>.mapLocalReviewsToUiReview(): Flow<List<UiReview>> =
        this.map { list ->
            list.map { review ->
                val user: User? = getReviewAuthor(review.authorUid)
                UiReview(
                    date = getFormattedDateFromEpochSeconds(review.timestamp),
                    authorName = user?.username ?: "",
                    authorUri = user?.image ?: "",
                    description = review.description,
                    rating = review.rating
                )
            }
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

data class UiReview(
    val date: String,
    val authorName: String,
    val authorUri: String,
    val description: String,
    val rating: Float
)
