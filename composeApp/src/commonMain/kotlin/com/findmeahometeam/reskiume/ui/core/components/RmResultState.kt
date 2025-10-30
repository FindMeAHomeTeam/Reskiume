package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryRed
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.general_error_unknown_message

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String = "") : UiState()
}

@Composable
fun RmResultState(uiState: UiState, customErrorMessage: String = "", onSuccess: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
        when (uiState) {
            UiState.Idle -> {} // Do nothing
            UiState.Loading -> {
                RmCircularProgressIndicator()
            }

            is UiState.Error -> {
                RmText(
                    text = uiState.message.ifBlank {
                        customErrorMessage.ifBlank {
                            stringResource(Res.string.general_error_unknown_message)
                        }
                    },
                    color = primaryRed
                )
            }

            UiState.Success -> {
                onSuccess()
            }
        }
    }
}
