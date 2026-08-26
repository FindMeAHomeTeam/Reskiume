package com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import kotlinx.coroutines.flow.firstOrNull

class InsertNonHumanAnimalInLocalRepository(
    private val manageImagePath: ManageImagePath,
    private val localNonHumanAnimalRepository: LocalNonHumanAnimalRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nonHumanAnimal: NonHumanAnimal,
        onInsertNonHumanAnimal: (rowId: Long) -> Unit
    ) {
        val imageFileName = manageImagePath.getFileNameFromLocalImagePath(nonHumanAnimal.imageUrl)

        localNonHumanAnimalRepository.insertNonHumanAnimal(
            nonHumanAnimal.copy(
                savedBy = getMyUid(),
                imageUrl = imageFileName
            ).toEntity(),
            onInsertNonHumanAnimal
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
