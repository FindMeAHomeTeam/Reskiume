package com.findmeahometeam.reskiume.domain.usecases.authUser

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateInAuthDataSource(private val repository: AuthRepository) {
    operator fun invoke(): Flow<AuthUser?> = repository.authState
}
