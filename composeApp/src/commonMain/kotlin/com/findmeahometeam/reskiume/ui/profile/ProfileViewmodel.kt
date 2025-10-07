package com.findmeahometeam.reskiume.ui.profile

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthState
import kotlinx.coroutines.flow.Flow

class ProfileViewmodel(
    private val observeAuthState: ObserveAuthState
) : ViewModel() {

    fun collectAuthState(): Flow<AuthUser?> = observeAuthState()
}