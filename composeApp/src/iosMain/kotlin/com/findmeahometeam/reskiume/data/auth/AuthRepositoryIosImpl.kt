package com.findmeahometeam.reskiume.data.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthCredential
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRAuthStateDidChangeListenerHandle
import cocoapods.FirebaseAuth.FIREmailAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.repository.AuthRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSError
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class AuthRepositoryIosImpl : AuthRepository {

    private val auth = FIRAuth.auth()
    private val _state = MutableStateFlow<AuthUser?>(null)
    private var handle: FIRAuthStateDidChangeListenerHandle? = null

    init {
        handle = auth.addAuthStateDidChangeListener { _, user: FIRUser? ->
            _state.value = user?.let {
                AuthUser(
                    uid = it.uid(),
                    name = it.displayName(),
                    email = it.email(),
                    photoUrl = user.photoURL().toString()
                )
            }
        }
    }

    override val authState: Flow<AuthUser?> = _state.asStateFlow()

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation: CancellableContinuation<AuthResult> ->
                auth.createUserWithEmail(
                    email = email,
                    password = password
                ) { result: FIRAuthDataResult?, error: NSError? ->
                    if (!continuation.isActive) return@createUserWithEmail

                    when {
                        // When there is an error
                        error != null -> {
                            continuation.resume(AuthResult.Error(message = error.localizedDescription))
                        }

                        // When the user is empty
                        result?.user() == null -> {
                            continuation.resume(AuthResult.Error(message = "Error! the user is empty"))
                        }

                        // If everything went well
                        else -> {
                            val user: FIRUser = result.user()
                            continuation.resume(
                                AuthResult.Success(
                                    user = AuthUser(
                                        uid = user.uid(),
                                        name = user.displayName(),
                                        email = user.email(),
                                        photoUrl = user.photoURL().toString()
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation: CancellableContinuation<AuthResult> ->
                auth.signInWithEmail(
                    email = email,
                    password = password
                ) { result: FIRAuthDataResult?, error: NSError? ->
                    if (!continuation.isActive) return@signInWithEmail

                    when {
                        // When there is an error
                        error != null -> {
                            continuation.resume(AuthResult.Error(message = error.localizedDescription))
                        }

                        // When the user is empty
                        result?.user() == null -> {
                            continuation.resume(AuthResult.Error(message = "Error! the user is empty"))
                        }

                        // If everything went well
                        else -> {
                            val user: FIRUser = result.user()
                            continuation.resume(
                                AuthResult.Success(
                                    user = AuthUser(
                                        uid = user.uid(),
                                        name = user.displayName(),
                                        email = user.email(),
                                        photoUrl = user.photoURL().toString()
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun signOut(): Boolean = auth.signOut(null)

    override suspend fun deleteUser(password: String, onDeleteUser: (String, String) -> Unit) {
        try {
            val email: String = auth.currentUser()?.email() ?: error("User is null")
            val uid: String = auth.currentUser()?.uid() ?: error("User is null")
            val authCredential: FIRAuthCredential =
                FIREmailAuthProvider.credentialWithEmail(email = email, password = password)

            auth.currentUser()
                ?.reauthenticateWithCredential(authCredential) { firAuthDataResult: FIRAuthDataResult?, error: NSError? ->
                    if (error == null) {
                        auth.currentUser()?.deleteWithCompletion { error: NSError? ->
                            if (error == null) {
                                onDeleteUser(uid, "")
                            } else {
                                onDeleteUser("", error.localizedDescription)
                            }
                        }
                    } else {
                        onDeleteUser("", error.localizedDescription)
                    }
                }
        } catch (e: Exception) {
            onDeleteUser("", e.message ?: "Unknown error")
        }
    }
}
