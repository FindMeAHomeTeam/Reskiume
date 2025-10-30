package com.findmeahometeam.reskiume.ui.profile.personalInformation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ModifyUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.ObserveAuthStateFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.SignOutFromAuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PersonalInformationViewmodel(
    observeAuthStateFromAuthDataSource: ObserveAuthStateFromAuthDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val modifyUserFromLocalDataSource: ModifyUserFromLocalDataSource,
    private val signOutFromAuthDataSource: SignOutFromAuthDataSource
): ViewModel() {

    private val authUserState: Flow<AuthUser?> = observeAuthStateFromAuthDataSource()

    @OptIn(ExperimentalTime::class)
    fun logOut() {
        viewModelScope.launch {
            authUserState.collect { authUser: AuthUser? ->
                if (authUser == null) return@collect

                val user: User = getUserFromLocalDataSource(authUser.uid)!!
                modifyUserFromLocalDataSource(user.copy(lastLogout = Clock.System.now().epochSeconds)) { rowsModified: Int ->
                    if(rowsModified > 0) {
                        Log.d("PersonalInformationViewmodel", "logOut: lastLogout updated successfully in local data source")
                    } else {
                        Log.e("PersonalInformationViewmodel", "logOut: failed to update lastLogout in local data source")
                    }
                    signOutFromAuthDataSource()
                }
            }
        }
    }
}
