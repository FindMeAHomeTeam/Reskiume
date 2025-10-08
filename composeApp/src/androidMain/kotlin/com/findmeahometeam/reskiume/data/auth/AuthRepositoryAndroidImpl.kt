package com.findmeahometeam.reskiume.data.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryAndroidImpl : AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    override val authState: Flow<AuthUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: AuthUser? = firebaseAuth.currentUser?.let { user: FirebaseUser ->
                AuthUser(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl.toString()
                )
            }
            trySend(user)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: error("Error creating the user account")
            AuthResult.Success(
                AuthUser(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl.toString()
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: error("User is null")
            AuthResult.Success(
                AuthUser(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl.toString()
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error", e)
        }
    }

    override fun signOut(): Boolean {
        auth.signOut()
        return true
    }

    override suspend fun deleteUser(password: String, onDeleteUser: (String, String) -> Unit) {
        try {
            val email: String = auth.currentUser?.email ?: error("User is null")
            val uid: String = auth.currentUser?.uid ?: error("User is null")
            val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, password)

            auth.currentUser?.reauthenticate(authCredential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onDeleteUser(uid, "")
                        } else {
                            onDeleteUser("", task.exception?.message ?: "Unknown error")
                        }
                    }
                } else {
                    onDeleteUser("", it.exception?.message ?: "Unknown error")
                }
            }
        } catch (e: Exception) {
            onDeleteUser("", e.message ?: "Unknown error")
        }
    }
}
