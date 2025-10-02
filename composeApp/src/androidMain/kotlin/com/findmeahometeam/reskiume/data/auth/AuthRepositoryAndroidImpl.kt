package com.findmeahometeam.reskiume.data.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryAndroidImpl: AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    override val authState: Flow<AuthUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: AuthUser? = firebaseAuth.currentUser?.let { user: FirebaseUser -> AuthUser(user.uid, user.email) }
            trySend(user)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: error("User is null")
            AuthResult.Success(AuthUser(uid = user.uid, email = user.email))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error", e)
        }
    }
}