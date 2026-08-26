package com.findmeahometeam.reskiume.domain.repository.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult

interface AuthRepositoryForIosDelegate {
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    fun signOut(): Boolean
    suspend fun updateUserEmail(
        password: String,
        newEmail: String,
        onUpdatedUserEmail: (String) -> Unit
    )
    suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String,
        onUpdatedUserPassword: (String) -> Unit
    )
    suspend fun deleteUser(password: String, onDeleteUser: (String) -> Unit)
}
