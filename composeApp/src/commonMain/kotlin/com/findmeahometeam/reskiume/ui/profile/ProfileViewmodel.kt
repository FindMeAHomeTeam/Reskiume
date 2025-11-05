package com.findmeahometeam.reskiume.ui.profile

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource
) : ViewModel() {

    val state: Flow<ProfileUiState> =
        observeAuthStateFromAuthDataSource().map { authUser: AuthUser? ->
            if (authUser?.uid == null) {
                Log.d("ProfileViewmodel", "User not logged in")
                ProfileUiState.Idle
            } else {
                val user: User? = getUserFromLocalDataSource(authUser.uid)
                if (user == null) {
                    Log.e("ProfileViewmodel", "User data not found")
                    ProfileUiState.Error("ProfileViewmodel - User data not found")
                } else {
                    ProfileUiState.Success(
                        user = user.copy(
                            email = authUser.email,
                            image = if (user.image.isBlank() || user.image == "null") "" else user.image,
                        )
                    )
                }
            }
        }

    sealed class ProfileUiState {
        object Idle : ProfileUiState()
        data class Success(val user: User) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
    }
}
