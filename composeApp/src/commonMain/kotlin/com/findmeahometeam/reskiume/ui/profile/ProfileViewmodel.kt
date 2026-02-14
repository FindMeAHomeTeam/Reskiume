package com.findmeahometeam.reskiume.ui.profile

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val log: Log
) : ViewModel() {
    val state: Flow<ProfileUiState> =
        observeAuthStateInAuthDataSource().map { authUser: AuthUser? ->
            if (authUser?.uid == null) {
                ProfileUiState.Idle
            } else {
                val user: User? = getUserFromLocalDataSource(authUser.uid)
                if (user == null) {
                    ProfileUiState.Error("ProfileViewmodel - User ${authUser.uid} not found")
                } else {
                    ProfileUiState.Success(
                        user = user.copy(
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

    fun logError(tag: String, message: String) {
        log.e(tag, message)
    }

    sealed class ProfileUiState {
        object Idle : ProfileUiState()
        data class Success(val user: User) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }
}
