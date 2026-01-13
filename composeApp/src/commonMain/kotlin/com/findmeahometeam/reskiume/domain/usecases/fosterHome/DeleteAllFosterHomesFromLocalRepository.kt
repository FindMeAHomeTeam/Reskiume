package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class DeleteAllFosterHomesFromLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {

    suspend operator fun invoke(
        ownerId: String,
        onDeleteAllFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        localFosterHomeRepository.deleteAllFosterHomes(ownerId, onDeleteAllFosterHomes)
    }
}
