package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.NonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertNonHumanAnimalInLocalRepository(
    private val nonHumanAnimalRepository: NonHumanAnimalRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nonHumanAnimal: NonHumanAnimal,
        onInsertNonHumanAnimal: (rowId: Long) -> Unit
    ) {
        nonHumanAnimalRepository.insertNonHumanAnimal(
            nonHumanAnimal.copy(savedBy = getMyUid()).toEntity(),
            onInsertNonHumanAnimal
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
