package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class DeleteAllMyFosterHomesFromLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {

    suspend operator fun invoke(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        localFosterHomeRepository.deleteAllMyFosterHomes(ownerId, onDeleteAllMyFosterHomes)
    }
}
