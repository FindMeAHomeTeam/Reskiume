package com.findmeahometeam.reskiume.domain.repository.remote.auth

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthUserRepositoryForIosDelegate {
    val authUserDelegateState: Flow<AuthUser?>
    fun updateAuthUserDelegate(delegate: AuthUser?)
}
