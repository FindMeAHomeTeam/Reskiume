package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.UiReview
import com.findmeahometeam.reskiume.uiReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCheckReviewsUtil(
    private val reviewsToCheck: List<UiReview> = listOf(uiReview)
) : CheckReviewsUtil {

    override fun getReviewListFlow(reviewedUid: String): Flow<List<UiReview>> =
        flowOf(reviewsToCheck)
}
