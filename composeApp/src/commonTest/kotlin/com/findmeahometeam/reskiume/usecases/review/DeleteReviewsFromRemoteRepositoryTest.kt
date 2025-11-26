package com.findmeahometeam.reskiume.usecases.review

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteReviewsFromRemoteRepositoryTest {

    val realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = mock {
        every {
            deleteRemoteReviews(review.reviewedUid, any())
        } returns Unit
    }

    private val deleteReviewsFromRemoteRepository: DeleteReviewsFromRemoteRepository =
        DeleteReviewsFromRemoteRepository(realtimeDatabaseRemoteReviewRepository)

    @Test
    fun `given remote reviews_when the app deletes them_then deleteRemoteReviews is called`() =
        runTest {
            deleteReviewsFromRemoteRepository(review.reviewedUid, {})
            verify {
                realtimeDatabaseRemoteReviewRepository.deleteRemoteReviews(review.reviewedUid, any())
            }
        }
}
