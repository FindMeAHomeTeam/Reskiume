package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class ModifyNonHumanAnimalInLocalRepository(
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nonHumanAnimal: NonHumanAnimal,
        onModifyNonHumanAnimal: (rowsUpdated: Int) -> Unit
    ) {
        localNonHumanAnimalRepository.modifyNonHumanAnimal(
            nonHumanAnimal.copy(savedBy = getMyUid()).toEntity(),
            onModifyNonHumanAnimal
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
