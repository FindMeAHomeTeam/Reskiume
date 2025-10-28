package com.findmeahometeam.reskiume.ui.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListButtonItem
import com.findmeahometeam.reskiume.ui.core.components.RmListSwitchItem
import com.findmeahometeam.reskiume.ui.core.components.RmSecondaryText
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.primaryBlue
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryBlue
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_advice
import reskiume.composeapp.generated.resources.ic_delete
import reskiume.composeapp.generated.resources.ic_feedback
import reskiume.composeapp.generated.resources.ic_foster_homes
import reskiume.composeapp.generated.resources.ic_indicator
import reskiume.composeapp.generated.resources.ic_notifications
import reskiume.composeapp.generated.resources.ic_paw
import reskiume.composeapp.generated.resources.ic_rating
import reskiume.composeapp.generated.resources.ic_rescue_events
import reskiume.composeapp.generated.resources.ic_user
import reskiume.composeapp.generated.resources.profile_screen_activist_title
import reskiume.composeapp.generated.resources.profile_screen_available_label
import reskiume.composeapp.generated.resources.profile_screen_delete_account_description
import reskiume.composeapp.generated.resources.profile_screen_delete_account_title
import reskiume.composeapp.generated.resources.profile_screen_feedback_description
import reskiume.composeapp.generated.resources.profile_screen_feedback_title
import reskiume.composeapp.generated.resources.profile_screen_get_advice_description
import reskiume.composeapp.generated.resources.profile_screen_get_advice_title
import reskiume.composeapp.generated.resources.profile_screen_indicator_content_description
import reskiume.composeapp.generated.resources.profile_screen_my_account_section
import reskiume.composeapp.generated.resources.profile_screen_my_activism_section
import reskiume.composeapp.generated.resources.profile_screen_my_foster_homes_description
import reskiume.composeapp.generated.resources.profile_screen_my_foster_homes_title
import reskiume.composeapp.generated.resources.profile_screen_my_rescue_events_description
import reskiume.composeapp.generated.resources.profile_screen_my_rescue_events_title
import reskiume.composeapp.generated.resources.profile_screen_non_human_animals_description
import reskiume.composeapp.generated.resources.profile_screen_non_human_animals_title
import reskiume.composeapp.generated.resources.profile_screen_personal_create_account_description
import reskiume.composeapp.generated.resources.profile_screen_personal_create_account_title
import reskiume.composeapp.generated.resources.profile_screen_personal_information_description
import reskiume.composeapp.generated.resources.profile_screen_personal_information_title
import reskiume.composeapp.generated.resources.profile_screen_profile_image_content_description
import reskiume.composeapp.generated.resources.profile_screen_rescue_notifications_description
import reskiume.composeapp.generated.resources.profile_screen_rescue_notifications_title
import reskiume.composeapp.generated.resources.profile_screen_reviews_description
import reskiume.composeapp.generated.resources.profile_screen_reviews_title
import reskiume.composeapp.generated.resources.profile_screen_settings_section
import reskiume.composeapp.generated.resources.profile_screen_unavailable_label
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun ProfileScreen(
    navigateToCreateAccountScreen: () -> Unit,
    navigateToPersonalInformationScreen: () -> Unit,
    navigateToDeleteAccountScreen: () -> Unit
) {
    val profileViewmodel: ProfileViewmodel = koinViewModel<ProfileViewmodel>()
    var uiUserModel: UiUserModel by remember { mutableStateOf(UiUserModel()) }
    val uiState: ProfileViewmodel.UiState by profileViewmodel.state.collectAsState(ProfileViewmodel.UiState.Idle)

    uiUserModel = if (uiState is ProfileViewmodel.UiState.Success) {
        (uiState as ProfileViewmodel.UiState.Success).uiUserModel.copy(
            username = (uiState as ProfileViewmodel.UiState.Success).uiUserModel.username.ifBlank {
                stringResource(
                    Res.string.profile_screen_activist_title
                )
            }
        )
    } else {
        UiUserModel(
            isRegistered = false,
            username = stringResource(Res.string.profile_screen_activist_title),
            image = ""
        )
    }
    val scrollState: ScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Header(uiUserModel)

        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_my_account_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Profile screen
        if (!uiUserModel.isRegistered) {
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_personal_create_account_title),
                description = stringResource(Res.string.profile_screen_personal_create_account_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_user,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToCreateAccountScreen()
                }
            )
        }

        // Reviews screen
        if (uiUserModel.isRegistered) {

            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_personal_information_title),
                description = stringResource(Res.string.profile_screen_personal_information_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_user,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToPersonalInformationScreen()
                }
            )

            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_reviews_title),
                description = stringResource(Res.string.profile_screen_reviews_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_rating,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )
        }

        // Notifications screen
        RmListSwitchItem(
            title = stringResource(Res.string.profile_screen_rescue_notifications_title),
            description = stringResource(Res.string.profile_screen_rescue_notifications_description),
            containerColor = backgroundColor,
            listAvatarType = RmListAvatarType.Icon(
                backgroundColor = tertiaryGreen,
                icon = Res.drawable.ic_notifications,
                iconColor = primaryGreen
            ),
            isChecked = uiUserModel.areNotificationsAvailable,
            onCheckedChange = {
                // TODO
            }
        )

        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_my_activism_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (uiUserModel.isRegistered) {

            // Non-human animals screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_non_human_animals_title),
                description = stringResource(Res.string.profile_screen_non_human_animals_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_paw,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )

            // Foster homes screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_my_foster_homes_title),
                description = stringResource(Res.string.profile_screen_my_foster_homes_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_foster_homes,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )

            // Rescue events screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_my_rescue_events_title),
                description = stringResource(Res.string.profile_screen_my_rescue_events_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_rescue_events,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )
        }

        // Advice screen
        RmListButtonItem(
            title = stringResource(Res.string.profile_screen_get_advice_title),
            description = stringResource(Res.string.profile_screen_get_advice_description),
            containerColor = backgroundColor,
            listAvatarType = RmListAvatarType.Icon(
                backgroundColor = tertiaryGreen,
                icon = Res.drawable.ic_advice,
                iconColor = primaryGreen
            ),
            onClick = {
                // TODO
            }
        )
        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_settings_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Feedback screen
        RmListButtonItem(
            title = stringResource(Res.string.profile_screen_feedback_title),
            description = stringResource(Res.string.profile_screen_feedback_description),
            containerColor = backgroundColor,
            listAvatarType = RmListAvatarType.Icon(
                backgroundColor = secondaryBlue,
                icon = Res.drawable.ic_feedback,
                iconColor = primaryBlue
            ),
            onClick = {
                // TODO
            }
        )

        // Delete account screen
        if (uiUserModel.isRegistered) {
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_delete_account_title),
                description = stringResource(Res.string.profile_screen_delete_account_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = secondaryRed,
                    icon = Res.drawable.ic_delete,
                    iconColor = primaryRed
                ),
                onClick = {
                    navigateToDeleteAccountScreen()
                }
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun Header(uiUserModel: UiUserModel) = uiUserModel.run {
    if (isRegistered && image.isNotBlank()) {
        AsyncImage(
            model = image,
            contentDescription =
                stringResource(Res.string.profile_screen_profile_image_content_description),
            modifier = Modifier.size(190.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            modifier = Modifier.size(190.dp),
            painter = painterResource(Res.drawable.reskiume),
            contentDescription =
                stringResource(Res.string.profile_screen_profile_image_content_description),
            tint = primaryGreen
        )
    }
    RmText(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        text = username,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black
    )
    Spacer(Modifier.height(16.dp))

    if (isRegistered && isAvailable) {
        Availability(stringResource(Res.string.profile_screen_available_label), secondaryGreen)
    } else if (isRegistered) {
        Availability(stringResource(Res.string.profile_screen_unavailable_label), gray)
    }
}

@Composable
private fun Availability(availability: String, availabilityColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(Res.drawable.ic_indicator),
            tint = availabilityColor,
            contentDescription = stringResource(Res.string.profile_screen_indicator_content_description)
        )
        Spacer(modifier = Modifier.width(8.dp))
        RmSecondaryText(availability)
    }
}
