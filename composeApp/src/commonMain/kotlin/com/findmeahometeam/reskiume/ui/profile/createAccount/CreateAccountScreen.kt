package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmPasswordTextField
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_account_screen_already_have_an_account
import reskiume.composeapp.generated.resources.create_account_screen_create_account_button
import reskiume.composeapp.generated.resources.create_account_screen_email_field_label
import reskiume.composeapp.generated.resources.create_account_screen_log_in

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(onBackPressed: () -> Unit, navigateToLoginScreen: () -> Unit) {

    val createAccountViewmodel: CreateAccountViewmodel = koinViewModel<CreateAccountViewmodel>()
    val uiState: CreateAccountViewmodel.UiState by createAccountViewmodel.state.collectAsState()

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
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Spacer(modifier = Modifier.weight(1f))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = email,
                label = stringResource(Res.string.create_account_screen_email_field_label),
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(5.dp))
            RmPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = pwd,
                onValueChange = { pwd = it }
            )
            //Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(Res.string.create_account_screen_create_account_button),
                enabled = isLogInButtonEnabled,
                onClick = {
                    createAccountViewmodel.createUserUsingEmailAndPwd(email, pwd)
                })
            Spacer(modifier = Modifier.height(15.dp))
            AlreadyHaveAnAccount(navigateToLoginScreen)
            Spacer(modifier = Modifier.height(10.dp))
            ResultState(uiState, onBackPressed)
        }
    }
}

@Composable
fun AlreadyHaveAnAccount(navigateToLoginScreen: () -> Unit) {

    val stringResource = stringResource(Res.string.create_account_screen_already_have_an_account)
    val stringResourceToLink = stringResource(Res.string.create_account_screen_log_in)
    val startIndex = stringResource.indexOf(stringResourceToLink)
    val endIndex = startIndex + stringResourceToLink.length
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        append(stringResource)
        addStyle(
            style = SpanStyle(color = primaryGreen),
            start = startIndex,
            end = endIndex
        )
        addLink(
            clickable = LinkAnnotation.Clickable(
                tag = stringResourceToLink,
                linkInteractionListener = {
                    navigateToLoginScreen()
                }),
            start = startIndex,
            end = endIndex
        )
    }
    Row {
        Text(
            text = annotatedLinkString,
            color = textColor
        )
    }
}

@Composable
private fun ResultState(uiState: CreateAccountViewmodel.UiState, onBackPressed: () -> Unit) {

    when (uiState) {
        CreateAccountViewmodel.UiState.Idle -> {} // Do nothing
        CreateAccountViewmodel.UiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = secondaryGreen,
                trackColor = Color.White,
            )
        }

        is CreateAccountViewmodel.UiState.Error -> {
            RmText(
                text = uiState.message,
                color = primaryRed
            )
        }

        CreateAccountViewmodel.UiState.Success -> {
            onBackPressed()
        }
    }
}
