package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.domain.repository.remote.AuthRepository

class SignOutFromAuthDataSource(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.signOut()
}