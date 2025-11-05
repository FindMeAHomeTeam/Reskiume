package com.findmeahometeam.reskiume.data.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthUserRepositoryForIosDelegate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalForeignApi::class)
class AuthRepositoryIosImpl(
    authUserRepositoryForIosDelegate: AuthUserRepositoryForIosDelegate,
    private val authRepositoryForIosDelegateWrapper: AuthRepositoryForIosDelegateWrapper
) : AuthRepository {

    override val authState: Flow<AuthUser?> =
        authUserRepositoryForIosDelegate.authUserDelegateState.also {
            Log.d("AuthRepositoryIosImpl", "authState: $it")
        }

    private suspend fun getDelegate(): AuthRepositoryForIosDelegate {
        return authRepositoryForIosDelegateWrapper.authRepositoryForIosDelegateState.filterNotNull()
            .first()
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult =
        getDelegate().createUserWithEmailAndPassword(
            email,
            password
        )

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AuthResult =
        getDelegate().signInWithEmailAndPassword(
            email,
            password
        )

    override fun signOut(): Boolean =
        authRepositoryForIosDelegateWrapper.authRepositoryForIosDelegateState.value?.signOut()
            ?: false

    override suspend fun updateUserEmail(
        password: String,
        newEmail: String,
        onUpdatedUserEmail: (String) -> Unit
    ) {
        getDelegate().updateUserEmail(
            password,
            newEmail,
            onUpdatedUserEmail
        )
    }

    override suspend fun updateUserPassword(
        currentPassword: String,
        newPassword: String,
        onUpdatedUserPassword: (String) -> Unit
    ) {
        getDelegate().updateUserPassword(
            currentPassword,
            newPassword,
            onUpdatedUserPassword
        )
    }

    override suspend fun deleteUser(
        password: String,
        onDeleteUser: (String) -> Unit
    ) {
        getDelegate().deleteUser(
            password,
            onDeleteUser
        )
    }
}
