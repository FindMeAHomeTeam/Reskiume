package com.findmeahometeam.reskiume.domain.usecases.review

import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.repository.local.LocalReviewRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertReviewInLocalRepository(
    private val localReviewRepository: LocalReviewRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(review: Review, onInsertReview: (rowId: Long) -> Unit) =
        localReviewRepository.insertLocalReview(review.copy(savedBy = getMyUid()).toEntity(), onInsertReview)

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
