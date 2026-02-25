package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

@Composable
fun RmManagePermission(
    permission: Permission,
    controller: PermissionsController = getPermissionController(),
    stringsForDialogExplainingPermission: StringsForDialog,
    stringsForDialogToOpenSettings: StringsForDialog,
    managePermissionState: ManagePermissionState,
    updateManagePermissionState: (managePermissionState: ManagePermissionState) -> Unit,
    onPermissionGranted: () -> Unit
) {

    when (managePermissionState) {
        ManagePermissionState.IDLE -> {}
        ManagePermissionState.CHECK_PERMISSION -> {

            RetrievePermissionStateEffect(
                permission = permission,
                controller = controller
            ) { permissionState: PermissionState ->

                when (permissionState) {
                    PermissionState.Granted -> {

                        updateManagePermissionState(ManagePermissionState.PERMISSION_GRANTED)
                    }
                    PermissionState.DeniedAlways -> {

                        updateManagePermissionState(ManagePermissionState.DISPLAY_DIALOG_OPEN_SETTINGS)
                    }
                    else -> {

                        updateManagePermissionState(ManagePermissionState.DISPLAY_DIALOG_EXPLAINING_PERMISSION)
                    }
                }
            }
        }
        ManagePermissionState.DISPLAY_DIALOG_OPEN_SETTINGS -> {

            RmDialog(
                emoji = stringsForDialogToOpenSettings.emoji,
                title = stringsForDialogToOpenSettings.title,
                message = stringsForDialogToOpenSettings.message,
                allowMessage = stringsForDialogToOpenSettings.allowMessage,
                denyMessage = stringsForDialogToOpenSettings.denyMessage,
                onClickAllow = {
                    controller.openAppSettings()
                    updateManagePermissionState(ManagePermissionState.IDLE)
                },
                onClickDeny = {
                    updateManagePermissionState(ManagePermissionState.IDLE)
                }
            )
        }
        ManagePermissionState.DISPLAY_DIALOG_EXPLAINING_PERMISSION -> {

            RmDialog(
                emoji = stringsForDialogExplainingPermission.emoji,
                title = stringsForDialogExplainingPermission.title,
                message = stringsForDialogExplainingPermission.message,
                allowMessage = stringsForDialogExplainingPermission.allowMessage,
                denyMessage = stringsForDialogExplainingPermission.denyMessage,
                onClickAllow = {
                    updateManagePermissionState(ManagePermissionState.DISPLAY_DIALOG_REQUESTING_PERMISSION)
                },
                onClickDeny = {
                    updateManagePermissionState(ManagePermissionState.IDLE)
                }
            )
        }
        ManagePermissionState.DISPLAY_DIALOG_REQUESTING_PERMISSION -> {

            PermissionRequestEffect(
                permission = permission,
                controller = controller,
                onResult = { isGranted ->
                    if (isGranted) {
                        updateManagePermissionState(ManagePermissionState.PERMISSION_GRANTED)
                    }
                },
                onAlwaysDenied = {
                    updateManagePermissionState(ManagePermissionState.ENABLE_DIALOG_OPEN_SETTINGS)
                },
                onDenied = {
                    updateManagePermissionState(ManagePermissionState.IDLE)
                }
            )
        }
        ManagePermissionState.ENABLE_DIALOG_OPEN_SETTINGS -> {

            RetrievePermissionStateEffect(
                permission = permission,
                controller = controller
            ) { permissionState: PermissionState ->

                // If Android enabled the dialog, but the permission is NotGranted instead of DeniedAlways, display the dialog to open the settings
                if (permissionState == PermissionState.NotGranted || permissionState == PermissionState.DeniedAlways) {
                    updateManagePermissionState(ManagePermissionState.DISPLAY_DIALOG_OPEN_SETTINGS)
                }
            }
        }
        ManagePermissionState.PERMISSION_GRANTED -> onPermissionGranted()
    }
}

@Composable
fun getPermissionController(): PermissionsController {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController =
        remember(factory) { factory.createPermissionsController() }
    return controller.also { BindEffect(it) }
}

@Composable
fun RetrievePermissionStateEffect(
    permission: Permission,
    controller: PermissionsController,
    onResult: (permissionState: PermissionState) -> Unit
) {
    LaunchedEffect(controller) {
        onResult(controller.getPermissionState(permission))
    }
}

@Composable
fun PermissionRequestEffect(
    permission: Permission,
    controller: PermissionsController,
    onResult: (isGranted: Boolean) -> Unit,
    onAlwaysDenied: () -> Unit,
    onDenied: () -> Unit
) {
    LaunchedEffect(controller) {
        try {
            controller.providePermission(permission)
            onResult(controller.isPermissionGranted(permission))

        } catch (_: DeniedAlwaysException) {

            onAlwaysDenied()
        } catch (_: DeniedException) {

            onDenied()
        }
    }
}

enum class ManagePermissionState {
    IDLE, CHECK_PERMISSION, PERMISSION_GRANTED, DISPLAY_DIALOG_EXPLAINING_PERMISSION, DISPLAY_DIALOG_REQUESTING_PERMISSION, ENABLE_DIALOG_OPEN_SETTINGS, DISPLAY_DIALOG_OPEN_SETTINGS
}

class StringsForDialog(
    val emoji: String,
    val title: String,
    val message: String,
    val allowMessage: String,
    val denyMessage: String,
)
