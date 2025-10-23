package com.findmeahometeam.reskiume.domain.repository.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult

interface AuthRepositoryForIosDelegate {
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    fun signOut(): Boolean
    suspend fun deleteUser(password: String, onDeleteUser: (String, String) -> Unit)
}
