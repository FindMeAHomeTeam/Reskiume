package com.findmeahometeam.reskiume.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource
) : ViewModel() {

    val state: StateFlow<UiState> = observeAuthStateFromAuthDataSource().map { authUser: AuthUser? ->
        if (authUser?.uid == null) {
            UiState.Error("User not logged in")
        } else {
            val user: User? = getUserFromLocalDataSource(authUser.uid)
            if (user == null) {
                return@map UiState.Error("User data not found")
            }
            UiState.Success(
                UiUserModel(
                    isRegistered = true,
                    username = user.username,
                    email = user.email,
                    image = if (user.image.isBlank() || user.image == "null") "" else user.image,
                    isAvailable = user.isAvailable,
                    areNotificationsAvailable = true //TODO
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Idle
    )

    sealed class UiState {
        object Idle : UiState()
        data class Success(val uiUserModel: UiUserModel) : UiState()
        data class Error(val message: String) : UiState()
    }
}

data class UiUserModel(
    val isRegistered: Boolean = false,
    val username: String = "",
    val email: String = "",
    val image: String = "",
    val isAvailable: Boolean = true,
    val areNotificationsAvailable: Boolean = true
)
