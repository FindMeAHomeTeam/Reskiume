package com.findmeahometeam.reskiume.ui.profile

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ProfileViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val log: Log
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val userState: Flow<UiState<User>> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->
            if (authUser?.uid == null) {
                flowOf(UiState.Idle())
            } else {
                getUserFromLocalDataSource(authUser.uid).map { user: User? ->
                    when {
                        user == null -> {
                            log.d(
                                "ProfileViewmodel",
                                "userState: User ${authUser.uid} not found"
                            )
                            UiState.Idle()
                        }

                        !user.isLoggedIn -> {
                            log.d(
                                "ProfileViewmodel",
                                "userState: User ${authUser.uid} is not logged in"
                            )
                            UiState.Idle()
                        }

                        else -> {
                            UiState.Success(
                                user.copy(
                                    email = authUser.email,
                                    image = if (user.image.isBlank() || user.image == "null") {
                                        ""
                                    } else {
                                        getImagePathForFileNameFromLocalDataSource(user.image)
                                    },
                                )
                            )
                        }
                    }
                }
            }
        }
}
