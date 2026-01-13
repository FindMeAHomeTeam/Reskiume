package com.findmeahometeam.reskiume.domain.usecases.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertFosterHomeInLocalRepository(
    private val localFosterHomeRepository: LocalFosterHomeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fosterHome: FosterHome,
        onInsertFosterHome: (rowId: Long) -> Unit
    ) {
        localFosterHomeRepository.insertFosterHome(
            fosterHome.copy(savedBy = getMyUid()).toEntity(),
            onInsertFosterHome
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
