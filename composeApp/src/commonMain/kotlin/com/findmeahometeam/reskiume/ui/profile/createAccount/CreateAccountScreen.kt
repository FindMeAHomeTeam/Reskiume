package com.findmeahometeam.reskiume.ui.profile.createAccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmPasswordTextField
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.create_account_screen_already_have_an_account
import reskiume.composeapp.generated.resources.create_account_screen_create_account_button
import reskiume.composeapp.generated.resources.create_account_screen_create_account_title
import reskiume.composeapp.generated.resources.create_account_screen_describe_yourself_field_label
import reskiume.composeapp.generated.resources.create_account_screen_email_field_label
import reskiume.composeapp.generated.resources.create_account_screen_log_in
import reskiume.composeapp.generated.resources.create_account_screen_name_field_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(onBackPressed: () -> Unit, navigateToLoginScreen: () -> Unit) {

    val createAccountViewmodel: CreateAccountViewmodel = koinViewModel<CreateAccountViewmodel>()
    val uiState: UiState by createAccountViewmodel.state.collectAsState()

    var name: String by rememberSaveable { mutableStateOf("") }
    var description: String by rememberSaveable { mutableStateOf("") }
    var imageUri: String by rememberSaveable { mutableStateOf("") }
    var email: String by rememberSaveable { mutableStateOf("") }
    val emailRegexPattern =
        Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    var pwd: String by rememberSaveable { mutableStateOf("") }
    val isLogInButtonEnabled by remember(name, email, pwd) {
        derivedStateOf {
            name.isNotBlank()
                    && email.isNotBlank()
                    && email.matches(emailRegexPattern)
                    && pwd.isNotBlank()
                    && pwd.length >= 6
        }
    }
    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(Res.string.create_account_screen_create_account_title),
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmAddPhoto {
                imageUri = it
            }
            Spacer(modifier = Modifier.height(15.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                label = stringResource(Res.string.create_account_screen_name_field_label),
                onValueChange = { name = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                text = description,
                label = stringResource(Res.string.create_account_screen_describe_yourself_field_label),
                onValueChange = { description = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = email,
                label = stringResource(Res.string.create_account_screen_email_field_label),
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            RmPasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = pwd,
                onValueChange = { pwd = it }
            )
            Spacer(modifier = Modifier.height(10.dp))
            RmResultState(uiState, onSuccess = onBackPressed)

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(Res.string.create_account_screen_create_account_button),
                enabled = isLogInButtonEnabled,
                onClick = {
                    createAccountViewmodel.createUserUsingEmailAndPwd(
                        user = User(
                            username = name,
                            description = description,
                            email = email,
                            image = imageUri
                        ),
                        password = pwd
                    )
                })
            Spacer(modifier = Modifier.height(15.dp))
            RmTextLink(
                text = stringResource(Res.string.create_account_screen_already_have_an_account),
                textToLink = stringResource(Res.string.create_account_screen_log_in),
                onClick = navigateToLoginScreen
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
