package com.findmeahometeam.reskiume.usecases.review

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetReviewsFromLocalRepositoryTest: CoroutineTestDispatcher() {

    val localReviewRepository: LocalReviewRepository = mock {
        everySuspend {
            getLocalReviews(review.reviewedUid)
        } returns flowOf(listOf(review.toEntity()))
    }

    private val getReviewsFromLocalRepository: GetReviewsFromLocalRepository =
        GetReviewsFromLocalRepository(localReviewRepository)

    @Test
    fun `given a local review_when the app inserts it_then insertLocalReview is called`() =
        runTest {
            getReviewsFromLocalRepository(review.reviewedUid).test {
                val actualReviews = awaitItem()
                assertEquals(listOf(review), actualReviews)
                awaitComplete()
            }
        }
}
