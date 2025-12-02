package com.findmeahometeam.reskiume.usecases.review

import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.review
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class InsertReviewsInLocalRepositoryTest {

    val localReviewRepository: LocalReviewRepository = mock {
        everySuspend {
            insertLocalReview(review.toEntity(), any())
        } returns Unit
    }

    val authRepository: AuthRepository = mock {
        everySuspend { authState } returns flowOf(authUser)
    }

    private val insertReviewInLocalRepository: InsertReviewInLocalRepository =
        InsertReviewInLocalRepository(localReviewRepository, authRepository)

    @Test
    fun `given a review_when the app inserts it in the local data source_then insertLocalReview is called`() =
        runTest {
            insertReviewInLocalRepository(review, {})
            verifySuspend {
                localReviewRepository.insertLocalReview(review.toEntity(), any())
            }
        }
}
