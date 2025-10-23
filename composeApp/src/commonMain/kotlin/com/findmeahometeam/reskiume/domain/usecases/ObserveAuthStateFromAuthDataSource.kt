package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class ObserveAuthStateFromAuthDataSource(private val repository: AuthRepository) {
    operator fun invoke() = repository.authState
}
