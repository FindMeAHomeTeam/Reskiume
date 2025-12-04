package com.findmeahometeam.reskiume.domain.usecases.authUser

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class SignOutFromAuthDataSource(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.signOut()
}