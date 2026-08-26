package com.findmeahometeam.reskiume.usecases.review

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetReviewsFromRemoteRepositoryTest: CoroutineTestDispatcher() {

    val realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = mock {
        every {
            getRemoteReviews(review.reviewedUid)
        } returns flowOf(listOf(review.toData()))
    }

    private val getReviewsFromRemoteRepository: GetReviewsFromRemoteRepository =
        GetReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

    @Test
    fun `given remote reviews_when the app retrieves them_then app gets a flow of list of Review`() =
        runTest {
            getReviewsFromRemoteRepository(review.reviewedUid).test {
                val actualReviews = awaitItem()
                assertEquals(listOf(review.copy(savedBy = "")), actualReviews)
                awaitComplete()
            }
        }
}
