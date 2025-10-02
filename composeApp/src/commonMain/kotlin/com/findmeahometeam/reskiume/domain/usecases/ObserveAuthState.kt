package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.AuthRepository

class ObserveAuthState(private val repository: AuthRepository) {

    operator fun invoke() = repository.authState

}