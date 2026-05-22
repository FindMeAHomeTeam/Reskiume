package com.findmeahometeam.reskiume.ui.profile.createReview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteMyChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CreateReview
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateReviewViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val insertReviewInRemoteRepository: InsertReviewInRemoteRepository,
    private val insertReviewInLocalRepository: InsertReviewInLocalRepository,
    private val deleteMyChatFromLocalRepository: DeleteMyChatFromLocalRepository,
    private val deleteRescueEventUtil: DeleteRescueEventUtil,
    private val log: Log
) : ViewModel() {

    private val activistIds: List<String> =
        saveStateHandleProvider.provideObjectRoute(CreateReview::class).allActivistIdsToReview

    private val chatId: String =
        saveStateHandleProvider.provideObjectRoute(CreateReview::class).chatId

    private val rescueEventId: String =
        saveStateHandleProvider.provideObjectRoute(CreateReview::class).rescueEventId

    private val creatorId: String =
        saveStateHandleProvider.provideObjectRoute(CreateReview::class).creatorId

    val activistsToReviewState: StateFlow<UiState<List<User>>> = flow {
        activistIds.map {
            getUserFromLocalDataSource(it).first()!!
        }.let { activistList ->
            emit(
                activistList.map {
                    it.copy(
                        image = getImagePathForFileNameFromLocalDataSource(
                            it.image
                        )
                    )
                }
            )
        }
    }.toUiState()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )

    private val myUid: StateFlow<String> = flow {
        observeAuthStateInAuthDataSource().first()?.uid?.let {
            emit(it)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ""
    )

    private val _submitReviewsState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Idle())
    val submitReviewsState: StateFlow<UiState<Unit>> = _submitReviewsState.asStateFlow()

    fun submitReviews(uiCreateReviews: List<UiCreateReview>) {

        _submitReviewsState.value = UiState.Loading()

        val reviews = uiCreateReviews.map { uiCreateReview ->
            Review(
                timestamp = 0,
                authorUid = myUid.value,
                reviewedUid = uiCreateReview.reviewedUid,
                description = uiCreateReview.description,
                rating = uiCreateReview.rating
            )
        }

        insertReviewsInRemoteDataSource(reviews) { updatedReviews ->

            insertReviewsInLocalDataSource(updatedReviews) {

                deleteMyChatFromLocalRepo {

                    if (rescueEventId.isNotEmpty()) {

                        deleteRescueEventUtil.deleteRescueEvent(
                            id = rescueEventId,
                            creatorId = creatorId,
                            coroutineScope = viewModelScope,
                            deleteOnLocal = true,
                            deleteOnRemote = false,
                            onError = {
                                _submitReviewsState.value = UiState.Error()
                            },
                            onComplete = {
                                _submitReviewsState.value = UiState.Success(Unit)
                            }
                        )
                    } else {
                        _submitReviewsState.value = UiState.Success(Unit)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun insertReviewsInRemoteDataSource(
        reviews: List<Review>,
        onSuccess: (updatedReviews: List<Review>) -> Unit
    ) {
        var counter = 0
        viewModelScope.launch {

            reviews.forEach { review ->

                insertReviewInRemoteRepository(review.copy(timestamp = Clock.System.now().toEpochMilliseconds())) {

                    if (it is DatabaseResult.Success) {
                        log.d(
                            "CreateReviewViewmodel",
                            "insertReviewInRemoteRepository: Review for ${review.reviewedUid} added to remote database"
                        )
                        if (counter == reviews.size - 1) {
                            onSuccess(reviews)
                        } else {
                            counter += 1
                        }
                    } else {
                        log.e(
                            "CreateReviewViewmodel",
                            "insertReviewInRemoteRepository: Error adding a review for ${review.reviewedUid} to remote database"
                        )
                    }
                }
            }
        }
    }

    private fun insertReviewsInLocalDataSource(
        reviews: List<Review>,
        onSuccess: () -> Unit
    ) {
        var counter = 0
        viewModelScope.launch {

            reviews.forEach { review ->

                insertReviewInLocalRepository(review) { rowId ->

                    if (rowId > 0) {
                        log.d(
                            "CreateReviewViewmodel",
                            "insertReviewsInLocalDataSource: Review for ${review.reviewedUid} added to local database"
                        )
                        if (counter == reviews.size - 1) {
                            onSuccess()
                        } else {
                            counter += 1
                        }
                    } else {
                        log.e(
                            "CreateReviewViewmodel",
                            "insertReviewsInLocalDataSource: Error adding a review for ${review.reviewedUid} to local database"
                        )
                    }
                }
            }
        }
    }

    private fun deleteMyChatFromLocalRepo(
        onSuccess: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {

            deleteMyChatFromLocalRepository(chatId) { rowsDeleted ->

                if (rowsDeleted > 0) {
                    log.d(
                        "CreateReviewViewmodel",
                        "deleteMyChatFromLocalRepo: Successfully deleted the chat $chatId from the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CreateReviewViewmodel",
                        "deleteMyChatFromLocalRepo: Something went wrong deleting the chat $chatId from the local data source"
                    )
                }
            }
        }
    }
}

data class UiCreateReview(
    val reviewedUid: String,
    val description: String,
    val rating: Float
)
