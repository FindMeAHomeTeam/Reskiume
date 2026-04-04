package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.runtime.Composable
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.manage_notification_permission_do_not_grant_permission_button
import reskiume.composeapp.generated.resources.manage_notification_permission_go_to_settings_title
import reskiume.composeapp.generated.resources.manage_notification_permission_grant_in_settings_message
import reskiume.composeapp.generated.resources.manage_notification_permission_grant_permission_button
import reskiume.composeapp.generated.resources.manage_notification_permission_message
import reskiume.composeapp.generated.resources.manage_notification_permission_open_settings_button
import reskiume.composeapp.generated.resources.manage_notification_permission_title

@Composable
fun RmManageNotificationPermission(
    permissionState: ManagePermissionState,
    onUpdatePermissionState: (ManagePermissionState) -> Unit
) {
    RmManagePermission(
        permission = Permission.REMOTE_NOTIFICATION,
        stringsForDialogExplainingPermission = StringsForDialog(
            emoji = "🔔",
            title = stringResource(Res.string.manage_notification_permission_title),
            message = stringResource(Res.string.manage_notification_permission_message),
            allowMessage = stringResource(Res.string.manage_notification_permission_grant_permission_button),
            denyMessage = stringResource(Res.string.manage_notification_permission_do_not_grant_permission_button)
        ),
        stringsForDialogToOpenSettings = StringsForDialog(
            emoji = "⚙️",
            title = stringResource(Res.string.manage_notification_permission_go_to_settings_title),
            message = stringResource(Res.string.manage_notification_permission_grant_in_settings_message),
            allowMessage = stringResource(Res.string.manage_notification_permission_open_settings_button),
            denyMessage = stringResource(Res.string.manage_notification_permission_do_not_grant_permission_button),
        ),
        managePermissionState = permissionState,
        updateManagePermissionState = onUpdatePermissionState
    )
}
