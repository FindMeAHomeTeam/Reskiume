package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository

class DeleteFosterHomeFromLocalRepository(private val localFosterHomeRepository: LocalFosterHomeRepository) {
    
    suspend operator fun invoke(
        id: String,
        onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit
    ) {
        localFosterHomeRepository.deleteFosterHome(id, onDeleteFosterHome)
    }
}
