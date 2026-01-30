package com.findmeahometeam.reskiume.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val log: Log,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource
) : ViewModel() {

    val state: StateFlow<UiState<Unit>> = observeAuthStateInAuthDataSource().map { authUser: AuthUser? ->
        if (authUser?.uid == null) {
            log.d("HomeViewmodel", "User not logged in")
            UiState.Idle()
        } else {
            val user: User? = getUserFromLocalDataSource(authUser.uid)
            if (user == null) {
                log.e("HomeViewmodel", "User ${authUser.uid} not found")
                UiState.Idle()
            } else {
                UiState.Success(Unit)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Idle()
    )
}
