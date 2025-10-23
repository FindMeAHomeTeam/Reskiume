package com.findmeahometeam.reskiume.data.remote.auth

import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepositoryForIosDelegateWrapper
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthUserRepositoryForIosDelegate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthRepositoryIosHelper: KoinComponent {
    val authUserRepositoryForIosDelegate: AuthUserRepositoryForIosDelegate by inject()
    val authRepositoryForIosDelegateWrapper: AuthRepositoryForIosDelegateWrapper by inject()
}
