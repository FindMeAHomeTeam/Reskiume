package com.findmeahometeam.reskiume.ui.profile.checkReviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class CheckAllReviewsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkReviewsUtil: CheckReviewsUtil,
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val checkActivistUtil: CheckActivistUtil
) : ViewModel() {

    private val reviewedUid = saveStateHandleProvider.provideObjectRoute(CheckAllReviews::class).uid

    val reviewListState: StateFlow<UiState<List<UiReview>>> =
        checkReviewsUtil.getReviewListFlow(reviewedUid).toUiState().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState.Loading()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val userDataIfNotMine: StateFlow<User?> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            if (authUser?.uid == reviewedUid) {
                flowOf(null)
            } else {
                flowOf(getActivist(authUser?.uid ?: " "))
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )

    private suspend fun getActivist(myUserUid: String): User? {

        return checkActivistUtil.getUser(reviewedUid, myUserUid)
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
