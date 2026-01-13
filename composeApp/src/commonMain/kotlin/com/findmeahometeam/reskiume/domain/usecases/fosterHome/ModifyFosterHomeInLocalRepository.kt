package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class ModifyFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        onModifyFosterHome: (rowsUpdated: Int) -> Unit
    ) {
        localFosterHomeRepository.modifyFosterHome(
            fosterHome.copy(savedBy = getMyUid()).toEntity(),
            onModifyFosterHome
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
