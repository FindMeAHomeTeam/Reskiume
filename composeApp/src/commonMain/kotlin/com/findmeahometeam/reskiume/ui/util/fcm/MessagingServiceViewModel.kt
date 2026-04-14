package com.findmeahometeam.reskiume.ui.util.fcm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessagingServiceViewModel : ViewModel(), KoinComponent {

    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource by inject()
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource by inject()

    fun retrieveActivistId(onActivistIdReceived: (String) -> Unit) {

        viewModelScope.launch {

            retrieveUserId(onActivistIdReceived)
        }
    }

    suspend fun retrieveUserId(onActivistIdReceived: (String) -> Unit) {

        val activistId = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: ""
        if (activistId.isBlank()) {
            onActivistIdReceived("")
        } else {
            val activist = getUserFromLocalDataSource(activistId).firstOrNull()
            if (activist?.isLoggedIn == true) {
                onActivistIdReceived(activistId)
            } else {
                onActivistIdReceived("")
            }
        }
    }
}
