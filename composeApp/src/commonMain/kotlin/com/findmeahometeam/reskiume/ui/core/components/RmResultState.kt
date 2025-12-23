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

sealed class UiState<T> {
    class Idle<T> : UiState<T>()
    class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String = "") : UiState<T>()
}

@Composable
fun <T> RmResultState(
    uiState: UiState<T>,
    customErrorMessage: String = "",
    onSuccess: @Composable (T) -> Unit
) {
    when (uiState) {
        is UiState.Idle -> { // Do nothing
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                contentAlignment = Alignment.Center,
                content = {}
            )
        }

        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                RmCircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                RmText(
                    text = uiState.message.ifBlank {
                        customErrorMessage.ifBlank {
                            stringResource(Res.string.general_error_unknown_message)
                        }
                    },
                    color = primaryRed
                )
            }
        }

        is UiState.Success -> {
            onSuccess(uiState.data)
        }
    }
}
