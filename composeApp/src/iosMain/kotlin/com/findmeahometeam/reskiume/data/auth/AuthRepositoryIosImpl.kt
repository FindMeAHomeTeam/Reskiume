package com.findmeahometeam.reskiume.data.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRAuthStateDidChangeListenerHandle
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
class AuthRepositoryIosImpl: AuthRepository {

    private val auth = FIRAuth.auth()
    private val _state = MutableStateFlow<AuthUser?>(null)
    private var handle: FIRAuthStateDidChangeListenerHandle? = null

    init {
        handle = auth.addAuthStateDidChangeListener { _, user: FIRUser? ->
            _state.value = user?.let { AuthUser(it.uid(), it.email()) }
        }
    }

    override val authState: Flow<AuthUser?> = _state.asStateFlow()

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation: CancellableContinuation<AuthResult> ->
                auth.signInWithEmail(email = email, password = password) { result: FIRAuthDataResult?, error: NSError? ->
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
                            continuation.resume(AuthResult.Success(user = AuthUser(uid = user.uid(), email = user.email())))
                        }
                    }
                }
            }
        }
    }
}