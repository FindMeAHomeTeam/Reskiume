package com.findmeahometeam.reskiume.usecases.review

import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertReviewsInLocalRepositoryTest {

    val localReviewRepository: LocalReviewRepository = mock {
        everySuspend {
            insertLocalReview(review.toEntity(), any())
        } returns Unit
    }

    private val insertReviewInLocalRepository: InsertReviewInLocalRepository =
        InsertReviewInLocalRepository(localReviewRepository)

    @Test
    fun `given a local review_when the app inserts it_then insertLocalReview is called`() =
        runTest {
            insertReviewInLocalRepository(review, {})
            verifySuspend {
                localReviewRepository.insertLocalReview(review.toEntity(), any())
            }
        }
}
