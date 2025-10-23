package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository

class CreateUserWithEmailAndPasswordFromAuthDataSource(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): AuthResult =
        repository.createUserWithEmailAndPassword(email, password)

}