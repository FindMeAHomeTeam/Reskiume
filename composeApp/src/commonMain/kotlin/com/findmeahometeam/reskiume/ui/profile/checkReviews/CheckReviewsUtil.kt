package com.findmeahometeam.reskiume.ui.profile.checkReviews

import kotlinx.coroutines.flow.Flow

interface CheckReviewsUtil {
    fun getReviewListFlow(reviewedUid: String): Flow<List<UiReview>>
}
