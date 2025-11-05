package com.findmeahometeam.reskiume.ui.profile.personalInformation

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmAddPhoto
import com.findmeahometeam.reskiume.ui.core.components.RmAvatar
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmCheckbox
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListSwitchItem
import com.findmeahometeam.reskiume.ui.core.components.RmPasswordTextField
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_notifications
import reskiume.composeapp.generated.resources.ic_warning
import reskiume.composeapp.generated.resources.user_screen_availability_description
import reskiume.composeapp.generated.resources.user_screen_available_label
import reskiume.composeapp.generated.resources.user_screen_change_your_password_checkbox_label
import reskiume.composeapp.generated.resources.user_screen_current_password_field_label
import reskiume.composeapp.generated.resources.user_screen_describe_yourself_field_label
import reskiume.composeapp.generated.resources.user_screen_email_field_label
import reskiume.composeapp.generated.resources.user_screen_log_out_account_message
import reskiume.composeapp.generated.resources.user_screen_log_out_text
import reskiume.composeapp.generated.resources.user_screen_name_field_label
import reskiume.composeapp.generated.resources.user_screen_new_password_field_label
import reskiume.composeapp.generated.resources.user_screen_save_changes_button
import reskiume.composeapp.generated.resources.user_screen_unavailable_label
import reskiume.composeapp.generated.resources.user_screen_user_account_title
import reskiume.composeapp.generated.resources.user_screen_verify_email_label

@Composable
fun PersonalInformationScreen(onBackPressed: () -> Unit) {

    val personalInformationViewmodel: PersonalInformationViewmodel =
        koinViewModel<PersonalInformationViewmodel>()
    val uiState: UiState by personalInformationViewmodel.uiState.collectAsState()

    val profileViewmodel: ProfileViewmodel = koinViewModel<ProfileViewmodel>()
    val profileUiState: ProfileViewmodel.ProfileUiState by profileViewmodel.state.collectAsState(
        initial = ProfileViewmodel.ProfileUiState.Idle
    )

    var user: User? by remember { mutableStateOf(null) }

    when (profileUiState) {
        is ProfileViewmodel.ProfileUiState.Idle -> {
            // Do nothing, wait for data
            return
        }

        is ProfileViewmodel.ProfileUiState.Error -> {
            Log.e(
                "PersonalInformationScreen",
                (profileUiState as ProfileViewmodel.ProfileUiState.Error).message
            )
            onBackPressed()
            return
        }

        else -> {
            user = (profileUiState as ProfileViewmodel.ProfileUiState.Success).user
        }
    }

    var name: String by rememberSaveable { mutableStateOf(user!!.username) }
    var description: String by rememberSaveable { mutableStateOf(user!!.description) }
    var imageUri: String by rememberSaveable { mutableStateOf(user!!.image) }
    var isAvailable: Boolean by rememberSaveable { mutableStateOf(user!!.isAvailable) }
    var email: String by rememberSaveable { mutableStateOf(user!!.email ?: "") }
    val emailRegexPattern =
        Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    var currentPassword: String by rememberSaveable { mutableStateOf("") }
    var isNewPassword: Boolean by rememberSaveable { mutableStateOf(false) }
    var newPassword: String by rememberSaveable { mutableStateOf("") }
    val isEmailAlertVisible by remember(email) {
        derivedStateOf {
            email != user!!.email && email.matches(emailRegexPattern)
        }
    }
    val isCurrentPasswordVisible by remember(
        isNewPassword,
        email
    ) {
        derivedStateOf {
            isNewPassword || email != user!!.email && email.matches(emailRegexPattern)
        }
    }
    val isUpdateUserButtonEnabled by remember(
        name,
        description,
        imageUri,
        email,
        isAvailable,
        currentPassword,
        newPassword
    ) {
        derivedStateOf {
            name.isNotBlank()
                    && email.matches(emailRegexPattern)
                    && (if (isCurrentPasswordVisible) currentPassword.length >= 6 else true)
                    && (if (isNewPassword) newPassword.length >= 6 else true)
                    && (name != user!!.username
                    || description != user!!.description
                    || imageUri != user!!.image
                    || email != user!!.email
                    || isAvailable != user!!.isAvailable
                    || (newPassword.isNotBlank() && newPassword == currentPassword))
        }
    }
    val scrollState = rememberScrollState()

    RmScaffold(
        title = stringResource(Res.string.user_screen_user_account_title, user!!.username),
        onBackPressed = onBackPressed,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(padding)
                .padding(horizontal = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RmAddPhoto(currentImageUri = imageUri) {
                imageUri = it
            }

            Spacer(modifier = Modifier.height(15.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                label = stringResource(Res.string.user_screen_name_field_label),
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(10.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                text = description,
                label = stringResource(Res.string.user_screen_describe_yourself_field_label),
                onValueChange = { description = it }
            )

            AnimatedVisibility(isEmailAlertVisible) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RmAvatar(
                            RmListAvatarType.Icon(
                                backgroundColor = tertiaryGreen,
                                icon = Res.drawable.ic_warning,
                                iconColor = primaryGreen
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        RmText(
                            text = stringResource(Res.string.user_screen_verify_email_label),
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            RmTextField(
                modifier = Modifier.fillMaxWidth(),
                text = email,
                label = stringResource(Res.string.user_screen_email_field_label),
                onValueChange = { email = it }
            )

            LaunchedEffect(isCurrentPasswordVisible) {
                if (!isCurrentPasswordVisible) {
                    currentPassword = ""
                }
            }
            AnimatedVisibility(isCurrentPasswordVisible) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    RmPasswordTextField(
                        label = stringResource(Res.string.user_screen_current_password_field_label),
                        modifier = Modifier.fillMaxWidth(),
                        password = currentPassword,
                        onValueChange = { currentPassword = it }
                    )
                }
            }

            LaunchedEffect(isNewPassword) {
                if (!isNewPassword) {
                    newPassword = ""
                }
            }
            AnimatedVisibility(isNewPassword) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    RmPasswordTextField(
                        label = stringResource(Res.string.user_screen_new_password_field_label),
                        modifier = Modifier.fillMaxWidth(),
                        password = newPassword,
                        onValueChange = { newPassword = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            RmListSwitchItem(
                title = if (isAvailable) {
                    stringResource(Res.string.user_screen_available_label)
                } else {
                    stringResource(Res.string.user_screen_unavailable_label)
                },
                description = stringResource(Res.string.user_screen_availability_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_notifications,
                    iconColor = primaryGreen
                ),
                isChecked = isAvailable,
                onCheckedChange = { isChecked ->
                    isAvailable = isChecked
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            RmCheckbox(stringResource(Res.string.user_screen_change_your_password_checkbox_label)) { isChecked ->
                isNewPassword = isChecked
            }

            Spacer(modifier = Modifier.height(10.dp))
            RmResultState(uiState, onSuccess = onBackPressed)
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.weight(1f))
            RmButton(
                text = stringResource(Res.string.user_screen_save_changes_button),
                enabled = isUpdateUserButtonEnabled,
                onClick = {
                    personalInformationViewmodel.saveUserChanges(
                        isDifferentEmail = email != user!!.email,
                        isDifferentImage = imageUri != user!!.image,
                        user = user!!.copy(
                            username = name,
                            description = description,
                            email = email,
                            image = imageUri,
                            isAvailable = isAvailable
                        ),
                        currentPassword = currentPassword,
                        newPassword = newPassword
                    )
                }
            )

            Spacer(modifier = Modifier.height(15.dp))
            RmTextLink(
                text = stringResource(Res.string.user_screen_log_out_account_message),
                textToLink = stringResource(Res.string.user_screen_log_out_text),
                onClick = {
                    personalInformationViewmodel.logOut()
                    onBackPressed()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
