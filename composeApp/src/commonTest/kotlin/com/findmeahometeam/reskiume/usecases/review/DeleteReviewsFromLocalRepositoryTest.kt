package com.findmeahometeam.reskiume.usecases.review

import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteReviewsFromLocalRepositoryTest {

    val localReviewRepository: LocalReviewRepository = mock {
        everySuspend {
            deleteLocalReviews(review.reviewedUid, any())
        } returns Unit
    }

    private val deleteReviewsFromLocalRepository: DeleteReviewsFromLocalRepository =
        DeleteReviewsFromLocalRepository(localReviewRepository)

    @Test
    fun `given local reviews_when the app deletes them_then deleteLocalReviews is called`() =
        runTest {
            deleteReviewsFromLocalRepository(review.reviewedUid, {})
            verifySuspend {
                localReviewRepository.deleteLocalReviews(review.reviewedUid, any())
            }
        }
}
