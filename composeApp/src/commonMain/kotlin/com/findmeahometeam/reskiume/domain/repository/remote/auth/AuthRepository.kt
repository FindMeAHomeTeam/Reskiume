package com.findmeahometeam.reskiume.domain.repository.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthUser?>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    fun signOut(): Boolean
    suspend fun updateUserEmail(
        password: String,
        newEmail: String,
        onUpdatedUserEmail: (error: String) -> Unit
    )

    suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String,
        onUpdatedUserPassword: (error: String) -> Unit
    )

    suspend fun deleteUser(password: String, onDeleteUser: (uid: String, error: String) -> Unit)
}
