package com.findmeahometeam.reskiume.domain.usecases

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.domain.repository.remote.AuthRepository

class SignInWithEmailAndPassword(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): AuthResult =
        repository.signInWithEmailAndPassword(email, password)

}