package com.findmeahometeam.reskiume.ui.home

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HomeViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val log: Log,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val userState: Flow<UiState<Unit>> = observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->
        if (authUser?.uid == null) {
            log.d("HomeViewmodel", "User not logged in")
            flowOf(UiState.Idle())
        } else {
            getUserFromLocalDataSource(authUser.uid).map { user: User? ->
                when {
                    user == null -> {
                        log.d("HomeViewmodel", "userState: User ${authUser.uid} not found")
                        UiState.Idle()
                    }

                    !user.isLoggedIn -> {
                        log.d("HomeViewmodel", "userState: User ${authUser.uid} is not logged in")
                        UiState.Idle()
                    }

                    else -> {
                        UiState.Success(Unit)
                    }
                }
            }
        }
    }
}
