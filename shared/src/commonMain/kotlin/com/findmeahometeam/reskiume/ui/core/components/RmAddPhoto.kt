package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.rememberImagePickerKMP
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.shared.generated.resources.Res
import reskiume.shared.generated.resources.add_photo_screen_no_camera_message
import reskiume.shared.generated.resources.add_photo_screen_no_camera_ok_button
import reskiume.shared.generated.resources.add_photo_screen_no_camera_title
import reskiume.shared.generated.resources.add_photo_screen_selected_photo_content_description
import reskiume.shared.generated.resources.camera_picker_go_to_settings_title
import reskiume.shared.generated.resources.camera_picker_grant_in_settings_message
import reskiume.shared.generated.resources.camera_picker_open_settings_button
import reskiume.shared.generated.resources.camera_picker_permission_do_not_grant_permission_button
import reskiume.shared.generated.resources.camera_picker_permission_grant_permission_button
import reskiume.shared.generated.resources.camera_picker_permission_message
import reskiume.shared.generated.resources.camera_picker_permission_title
import reskiume.shared.generated.resources.create_account_screen_add_photo_message
import reskiume.shared.generated.resources.create_account_screen_delete_message
import reskiume.shared.generated.resources.create_account_screen_from_camera_message
import reskiume.shared.generated.resources.create_account_screen_from_gallery_message
import reskiume.shared.generated.resources.ic_add_photo
import reskiume.shared.generated.resources.ic_close

@Composable
fun RmAddPhoto(
    pickMultiplePhotosFromGallery: Boolean = false,
    currentImageUri: String = "",
    onUriRetrieved: (String) -> Unit,
    onDeleteDiscardedImage: (String) -> Unit
) {
    var showAddPhoto by remember { mutableStateOf(currentImageUri.isBlank()) }
    var showGallery by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var uri by remember { mutableStateOf(currentImageUri) }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(showAddPhoto) {
            Column(
                modifier = Modifier.wrapContentSize()
                    .background(color = tertiaryGreen, shape = RoundedCornerShape(15.dp))
                    .border(
                        BorderStroke(2.dp, gray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(Res.drawable.ic_add_photo),
                    contentDescription = null,
                    tint = primaryGreen
                )
                Spacer(modifier = Modifier.height(10.dp))
                RmText(
                    text = stringResource(Res.string.create_account_screen_add_photo_message),
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    RmButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.create_account_screen_from_gallery_message),
                        onClick = { showGallery = true }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    RmButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(Res.string.create_account_screen_from_camera_message),
                        onClick = { showCamera = true }
                    )
                }
            }
        }

        if (showGallery) {
            InvokeGalleryPicker(pickMultiplePhotosFromGallery) { shouldShowGallery, uriProvided ->

                showGallery = shouldShowGallery
                if (uriProvided.isNotBlank()) {

                    showAddPhoto = false
                    uri = uriProvided
                    onUriRetrieved(uriProvided)
                }
            }
        }

        if (showCamera) {
            InvokeCameraPicker { shouldShowCamera, uriProvided ->

                showCamera = shouldShowCamera
                if (uriProvided.isNotBlank()) {

                    showAddPhoto = false
                    uri = uriProvided
                    onUriRetrieved(uriProvided)
                }
            }
        }

        AnimatedVisibility(uri.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (uri.isNotBlank()) { // It avoids a visual glitch for the delete button when the image is deleted

                    Box(contentAlignment = Alignment.TopEnd) {
                        RmImage(
                            imagePath = uri,
                            contentDescription = stringResource(Res.string.add_photo_screen_selected_photo_content_description),
                            modifier = Modifier.fillMaxSize()
                        )
                        Row(
                            modifier = Modifier.wrapContentSize()
                                .padding(16.dp)
                                .background(color = secondaryRed, shape = RoundedCornerShape(15.dp))
                                .padding(8.dp)
                                .clickable {
                                    showAddPhoto = true
                                    if (uri.contains("file:///")) {
                                        onDeleteDiscardedImage(uri) // delete the discarded image if cached
                                    }
                                    uri = ""
                                    onUriRetrieved(uri)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(Res.drawable.ic_close),
                                contentDescription = "remove photo",
                                tint = primaryRed
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            RmText(
                                text = stringResource(Res.string.create_account_screen_delete_message),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = primaryRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InvokeGalleryPicker(
    pickMultiplePhotosFromGallery: Boolean,
    onGalleryPickerResult: (Boolean, String) -> Unit
) {
    val picker = rememberImagePickerKMP()
    LaunchedEffect(Unit) {
        picker.launchGallery(
            allowMultiple = pickMultiplePhotosFromGallery,
            mimeTypes = listOf(MimeType.IMAGE_ALL),
            compressionLevel = CompressionLevel.HIGH
        )
    }
    when (val result = picker.result) {
        ImagePickerResult.Idle, ImagePickerResult.Loading -> Unit

        is ImagePickerResult.Success -> onGalleryPickerResult(false, result.first?.uri ?: "")

        is ImagePickerResult.Error, ImagePickerResult.Dismissed -> onGalleryPickerResult(false, "")
    }
}

@Composable
private fun InvokeCameraPicker(onCameraPickerResult: (Boolean, String) -> Unit) {

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH,
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customDeniedDialog = { onRetry: () -> Unit, onDismiss: () -> Unit ->
                        RmDialog(
                            emoji = "📸",
                            title = stringResource(Res.string.camera_picker_permission_title),
                            message = stringResource(Res.string.camera_picker_permission_message),
                            allowMessage = stringResource(Res.string.camera_picker_permission_grant_permission_button),
                            denyMessage = stringResource(Res.string.camera_picker_permission_do_not_grant_permission_button),
                            onClickAllow = onRetry,
                            onClickDeny = {
                                onDismiss()
                                onCameraPickerResult(false, "")
                            }
                        )
                    },
                    customSettingsDialog = { onOpenSettings: () -> Unit, onDismiss: () -> Unit ->
                        RmDialog(
                            emoji = "⚙️",
                            title = stringResource(Res.string.camera_picker_go_to_settings_title),
                            message = stringResource(Res.string.camera_picker_grant_in_settings_message),
                            allowMessage = stringResource(Res.string.camera_picker_open_settings_button),
                            denyMessage = stringResource(Res.string.camera_picker_permission_do_not_grant_permission_button),
                            onClickAllow = onOpenSettings,
                            onClickDeny = {
                                onDismiss()
                                onCameraPickerResult(false, "")
                            }
                        )
                    }
                )
            )
        )
    )
    LaunchedEffect(Unit) {
        picker.launchCamera(cameraCaptureConfig = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH))
    }

    when (val result = picker.result) {
        ImagePickerResult.Idle, ImagePickerResult.Loading -> Unit

        is ImagePickerResult.Success -> onCameraPickerResult(false, result.first?.uri ?: "")

        ImagePickerResult.Dismissed -> onCameraPickerResult(false, "")

        is ImagePickerResult.Error -> {

            var displayNoCameraError by rememberSaveable { mutableStateOf(true) }
            var fallbackToGallery by rememberSaveable { mutableStateOf(false) }

            if (displayNoCameraError) {

                RmDialog(
                    emoji = "📸",
                    title = stringResource(Res.string.add_photo_screen_no_camera_title),
                    message = stringResource(Res.string.add_photo_screen_no_camera_message),
                    allowMessage = stringResource(Res.string.add_photo_screen_no_camera_ok_button),
                    onClickAllow = {
                        displayNoCameraError = false
                        fallbackToGallery = true
                    },
                    onClickDeny = {
                        displayNoCameraError = false
                        onCameraPickerResult(false, "")
                    }
                )
            }
            if (fallbackToGallery) {

                LaunchedEffect(Unit) {
                    picker.launchGallery(
                        allowMultiple = false,
                        mimeTypes = listOf(MimeType.IMAGE_ALL),
                        compressionLevel = CompressionLevel.HIGH
                    )
                }
            }
        }
    }
}
