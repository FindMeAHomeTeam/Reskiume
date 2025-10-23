package com.findmeahometeam.reskiume.data.remote.auth

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegate
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryForIosDelegateWrapperImpl: AuthRepositoryForIosDelegateWrapper {

    private val _authRepositoryForIosDelegateState: MutableStateFlow<AuthRepositoryForIosDelegate?> = MutableStateFlow(null)

    override val authRepositoryForIosDelegateState: StateFlow<AuthRepositoryForIosDelegate?> = _authRepositoryForIosDelegateState.asStateFlow()

    override fun updateAuthRepositoryForIosDelegate(delegate: AuthRepositoryForIosDelegate?) {
        _authRepositoryForIosDelegateState.value = delegate
    }
}
