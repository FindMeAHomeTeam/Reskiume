package com.findmeahometeam.reskiume.data.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthUserRepositoryForIosDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthUserRepositoryForIosDelegateImpl: AuthUserRepositoryForIosDelegate {
    private val _authUserDelegateState: MutableStateFlow<AuthUser?> = MutableStateFlow(null)

    override val authUserDelegateState: Flow<AuthUser?> = _authUserDelegateState.asStateFlow()

    override fun updateAuthUserDelegate(delegate: AuthUser?) {
        _authUserDelegateState.update { delegate }
        Log.d("AuthUserRepositoryForIosDelegateImpl", "updateAuthUserDelegate: $delegate")
    }
}
