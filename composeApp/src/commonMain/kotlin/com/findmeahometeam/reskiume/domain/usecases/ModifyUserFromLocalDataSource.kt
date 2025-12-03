package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class ModifyUserFromLocalDataSource(
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, onModifyUser: (rowsUpdated: Int) -> Unit) {
        localUserRepository.modifyUser(user.copy(savedBy = getMyUid()), onModifyUser)
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
