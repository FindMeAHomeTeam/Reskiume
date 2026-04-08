package com.findmeahometeam.reskiume.data.util.fcm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class MessagingServiceViewModel(
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val stringProvider: StringProvider
): ViewModel() {

    fun retrieveActivistId(
        onActivistIdReceived: (String) -> Unit
    ) {
        viewModelScope.launch {

            val activistId = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: ""
            onActivistIdReceived(activistId)
        }
    }

    fun retrieveStringResource(
        stringResource: StringResource,
        onStringResourceReceived: (String) -> Unit
    ) {
        viewModelScope.launch {

            val text = stringProvider.getStringResource(stringResource)
            onStringResourceReceived(text)
        }
    }
}
