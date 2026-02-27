package com.findmeahometeam.reskiume.ui.profile.checkReviews

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

class CheckReviewsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkReviewsUtil: CheckReviewsUtil,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val checkActivistUtil: CheckActivistUtil
) : ViewModel() {

    private val reviewedUid = saveStateHandleProvider.provideObjectRoute(CheckReviews::class).uid

    val reviewListFlow: Flow<List<UiReview>> = checkReviewsUtil.getReviewListFlow(reviewedUid)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDataIfNotMine(): Flow<User?> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            if (authUser?.uid == reviewedUid) {
                flowOf(null)
            } else {
                flowOf(getActivist(authUser?.uid ?: ""))
            }
        }

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
