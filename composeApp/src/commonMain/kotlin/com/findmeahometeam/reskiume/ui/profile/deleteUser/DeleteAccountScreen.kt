package com.findmeahometeam.reskiume.ui.profile.deleteUser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmAvatar
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCircularProgressIndicator
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmPasswordTextField
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryTextColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.delete_account_screen_are_you_sure_delete_account_message
import reskiume.composeapp.generated.resources.delete_account_screen_delete_account_button
import reskiume.composeapp.generated.resources.delete_account_screen_delete_account_title
import reskiume.composeapp.generated.resources.delete_account_screen_delete_message
import reskiume.composeapp.generated.resources.delete_account_screen_explanation_delete_account_message
import reskiume.composeapp.generated.resources.ic_warning

@Composable
fun DeleteAccountScreen(onBackPressed: () -> Unit) {

    val deleteAccountViewmodel: DeleteAccountViewmodel = koinViewModel<DeleteAccountViewmodel>()
    val uiState: DeleteAccountViewmodel.UiState by deleteAccountViewmodel.state.collectAsState()

    var password by remember { mutableStateOf("") }
    val buttonEnabled by remember(password) {
        derivedStateOf {
            password.isNotBlank() && password.length >= 6
        }
    }

    RmScaffold(
        title = stringResource(Res.string.delete_account_screen_delete_account_title),
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RmAvatar(
                    RmListAvatarType.Icon(
                        backgroundColor = secondaryRed,
                        icon = Res.drawable.ic_warning,
                        iconColor = primaryRed
                    )
                )
                Spacer(Modifier.height(16.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.delete_account_screen_are_you_sure_delete_account_message),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(5.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.delete_account_screen_explanation_delete_account_message),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = secondaryTextColor
                )
                Spacer(Modifier.height(5.dp))
                RmPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    password = password,
                    onValueChange = { password = it }
                )
                Spacer(Modifier.height(10.dp))
                ResultState(uiState, onSuccessfulDelete = onBackPressed)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RmButton(
                    text = stringResource(Res.string.delete_account_screen_delete_account_button),
                    containerColor = primaryRed,
                    enabled = buttonEnabled
                ) {
                    deleteAccountViewmodel.deleteAccount(password)
                }
            }
        }
    }
}

@Composable
private fun ResultState(uiState: DeleteAccountViewmodel.UiState, onSuccessfulDelete: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
        when (uiState) {
            DeleteAccountViewmodel.UiState.Idle -> {} // Do nothing
            DeleteAccountViewmodel.UiState.Loading -> {
                RmCircularProgressIndicator()
            }

            is DeleteAccountViewmodel.UiState.Error -> {
                RmText(
                    text = uiState.message.ifBlank { stringResource(Res.string.delete_account_screen_delete_message) },
                    color = primaryRed
                )
            }

            DeleteAccountViewmodel.UiState.Success -> {
                onSuccessfulDelete()
            }
        }
    }
}
