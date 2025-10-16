package com.findmeahometeam.reskiume.ui.home

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import kotlinx.coroutines.flow.Flow

class HomeViewmodel(
    private val observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource
) : ViewModel() {

    fun collectAuthState(): Flow<AuthUser?> = observeAuthStateFromAuthDataSource()
}