package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.manage_location_permission_do_not_grant_permission_button
import reskiume.composeapp.generated.resources.manage_location_permission_go_to_settings_title
import reskiume.composeapp.generated.resources.manage_location_permission_grant_in_settings_message
import reskiume.composeapp.generated.resources.manage_location_permission_grant_permission_button
import reskiume.composeapp.generated.resources.manage_location_permission_open_settings_button
import reskiume.composeapp.generated.resources.manage_location_permission_title
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_later_location_button
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_location_open_settings_button
import reskiume.composeapp.generated.resources.manage_location_permission_turn_on_location_title

@Composable
fun RmManageLocationPermission(
    explainingLocationPermissionMessage: String,
    explainingLocationActivationMessage: String,
    permissionState: ManagePermissionState,
    isLocationEnabledState: State<Boolean>,
    onRequestEnableLocation: suspend () -> Boolean,
    onUpdateLocation: suspend () -> Unit,
    onBackPressed: () -> Unit,
    onUpdatePermissionState: (ManagePermissionState) -> Unit
) {
    var displayDialogToRequestLocationActivation: Boolean by rememberSaveable(!isLocationEnabledState.value) {
        mutableStateOf(
            !isLocationEnabledState.value
        )
    }
    var isAllowClicked: Boolean by rememberSaveable { mutableStateOf(false) }

    RmManagePermission(
        permission = Permission.COARSE_LOCATION,
        stringsForDialogExplainingPermission = StringsForDialog(
            emoji = "📍",
            title = stringResource(Res.string.manage_location_permission_title),
            message = explainingLocationPermissionMessage,
            allowMessage = stringResource(Res.string.manage_location_permission_grant_permission_button),
            denyMessage = stringResource(Res.string.manage_location_permission_do_not_grant_permission_button)
        ),
        stringsForDialogToOpenSettings = StringsForDialog(
            emoji = "⚙️",
            title = stringResource(Res.string.manage_location_permission_go_to_settings_title),
            message = stringResource(Res.string.manage_location_permission_grant_in_settings_message),
            allowMessage = stringResource(Res.string.manage_location_permission_open_settings_button),
            denyMessage = stringResource(Res.string.manage_location_permission_do_not_grant_permission_button),
        ),
        managePermissionState = permissionState,
        updateManagePermissionState = onUpdatePermissionState,
        onPermissionGranted = {}
    )

    if (permissionState == ManagePermissionState.PERMISSION_GRANTED) {

        if (displayDialogToRequestLocationActivation) {

            RmDialog(
                emoji = "⚙️",
                title = stringResource(Res.string.manage_location_permission_turn_on_location_title),
                message = explainingLocationActivationMessage,
                allowMessage = stringResource(Res.string.manage_location_permission_turn_on_location_open_settings_button),
                denyMessage = stringResource(Res.string.manage_location_permission_turn_on_later_location_button),
                onClickAllow = {
                    isAllowClicked = true
                },
                onClickDeny = {
                    displayDialogToRequestLocationActivation = false
                    onBackPressed()
                }
            )
            LaunchedEffect(isAllowClicked) {

                if (isAllowClicked) {
                    val isEnabled: Boolean = onRequestEnableLocation()
                    if (isEnabled) {
                        displayDialogToRequestLocationActivation = false
                    }
                }
            }
        } else {
            LaunchedEffect(isLocationEnabledState.value) {

                if (isLocationEnabledState.value) {
                    onUpdateLocation()
                }
            }
        }
    }
}
