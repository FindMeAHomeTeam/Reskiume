package com.findmeahometeam.reskiume.ui.profile.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCircularProgressIndicator
import com.findmeahometeam.reskiume.ui.core.components.RmPasswordTextField
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.primaryRed
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.general_error_unknown_message
import reskiume.composeapp.generated.resources.login_screen_email_field_label
import reskiume.composeapp.generated.resources.login_screen_log_in_button
import reskiume.composeapp.generated.resources.login_screen_log_in_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBackPressed: () -> Unit, onLoginSuccessful: () -> Unit) {

    val loginViewmodel: LoginViewmodel = koinViewModel<LoginViewmodel>()
    val uiState: LoginViewmodel.UiState by loginViewmodel.state.collectAsState()

    var email: String by rememberSaveable { mutableStateOf("") }
    val emailRegexPattern =
        Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    var pwd: String by rememberSaveable { mutableStateOf("") }
    val isLogInButtonEnabled by remember(email, pwd) {
        derivedStateOf {
            email.isNotEmpty() && email.matches(emailRegexPattern) && pwd.isNotBlank() && pwd.length >= 6
        }
    }

    RmScaffold(
        title = stringResource(Res.string.login_screen_log_in_title),
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = email,
                label = stringResource(Res.string.login_screen_email_field_label),
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(5.dp))
            RmPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = pwd,
                onValueChange = { pwd = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ResultState(uiState, onLoginSuccessful)

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(Res.string.login_screen_log_in_button),
                enabled = isLogInButtonEnabled,
                onClick = {
                    loginViewmodel.signInUsingEmail(email, pwd)
                })
        }
    }
}

@Composable
private fun ResultState(uiState: LoginViewmodel.UiState, onLoginSuccessful: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
        when (uiState) {
            LoginViewmodel.UiState.Idle -> {} // Do nothing
            LoginViewmodel.UiState.Loading -> {
                RmCircularProgressIndicator()
            }

            is LoginViewmodel.UiState.Error -> {
                RmText(
                    text = uiState.message.ifBlank { stringResource(Res.string.general_error_unknown_message) },
                    color = primaryRed
                )
            }

            LoginViewmodel.UiState.Success -> {
                onLoginSuccessful()
            }
        }
    }
}