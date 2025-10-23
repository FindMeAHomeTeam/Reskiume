package com.findmeahometeam.reskiume.domain.repository.remote.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepositoryForIosDelegateWrapper {
    val authRepositoryForIosDelegateState: StateFlow<AuthRepositoryForIosDelegate?>
    fun updateAuthRepositoryForIosDelegate(delegate: AuthRepositoryForIosDelegate?)
}
