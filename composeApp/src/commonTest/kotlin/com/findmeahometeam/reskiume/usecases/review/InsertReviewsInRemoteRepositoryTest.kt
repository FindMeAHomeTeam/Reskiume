package com.findmeahometeam.reskiume.usecases.review

import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInRemoteRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertReviewsInRemoteRepositoryTest {

    val realtimeDatabaseRemoteReviewRepository: RealtimeDatabaseRemoteReviewRepository = mock {
        everySuspend {
            insertRemoteReview(review.toData(), any())
        } returns Unit
    }

    private val insertReviewInRemoteRepository: InsertReviewInRemoteRepository =
        InsertReviewInRemoteRepository(realtimeDatabaseRemoteReviewRepository)

    @Test
    fun `given a review_when the app inserts it in the remote repository_then insertRemoteReview is called`() =
        runTest {
            insertReviewInRemoteRepository(review, {})
            verifySuspend {
                realtimeDatabaseRemoteReviewRepository.insertRemoteReview(review.toData(), any())
            }
        }
}
