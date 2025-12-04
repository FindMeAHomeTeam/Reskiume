package com.findmeahometeam.reskiume.domain.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class ObserveAuthStateInAuthDataSource(private val repository: AuthRepository) {
    operator fun invoke() = repository.authState
}
