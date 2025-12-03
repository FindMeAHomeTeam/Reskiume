package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertUserToLocalDataSource(
    private val localUserRepository: LocalUserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, onInsertUser: (rowId: Long) -> Unit) {
        localUserRepository.insertUser(user.copy(savedBy = getMyUid()), onInsertUser)
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""
}
