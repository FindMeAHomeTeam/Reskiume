package com.findmeahometeam.reskiume.ui.integration.fakes

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository(
    private var authUser: AuthUser? = null,
    private var authEmail: String? = null,
    private var authPassword: String? = null
) : AuthRepository {

    override val authState: Flow<AuthUser?>
        get() = flowOf(authUser)

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        if (email == authEmail) return AuthResult.Error("User already exists")

        authEmail = email
        authPassword = password
        val newUser = AuthUser(
            uid = user.uid,
            name = user.username,
            email = email,
            photoUrl = null
        )
        authUser = newUser
        return AuthResult.Success(newUser)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return if (email == authEmail && password == authPassword) {
            AuthResult.Success(authUser!!)
        } else {
            AuthResult.Error("Invalid credentials")
        }
    }

    override fun signOut(): Boolean {
        authUser = null
        return true
    }

    override suspend fun updateUserEmail(
        password: String,
        newEmail: String,
        onUpdatedUserEmail: (error: String) -> Unit
    ) {
        if (password == authPassword) {
            authEmail = newEmail
            authUser = authUser?.copy(email = newEmail)
            onUpdatedUserEmail("")
        } else {
            onUpdatedUserEmail("Incorrect password")
        }
    }

    override suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String,
        onUpdatedUserPassword: (error: String) -> Unit
    ) {
        if (currentPassword == authPassword) {
            authPassword = newPassword
            onUpdatedUserPassword("")
        } else {
            onUpdatedUserPassword("Incorrect current password")
        }
    }

    override suspend fun deleteUser(
        password: String,
        onDeleteUser: (error: String) -> Unit
    ) {
        if (password == authPassword) {
            authUser = null
            authEmail = null
            authPassword = null
            onDeleteUser("")
        } else {
            onDeleteUser("Incorrect password")
        }
    }
}
