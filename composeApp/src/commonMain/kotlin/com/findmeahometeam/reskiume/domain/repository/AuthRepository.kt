package com.findmeahometeam.reskiume.domain.repository

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthUser?>
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult

}