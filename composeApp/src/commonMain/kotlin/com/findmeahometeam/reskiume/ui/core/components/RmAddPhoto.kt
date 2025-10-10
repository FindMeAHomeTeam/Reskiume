package com.findmeahometeam.reskiume.ui.core.components

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.camera_picker_go_to_settings_title
import reskiume.composeapp.generated.resources.camera_picker_grant_in_settings_message
import reskiume.composeapp.generated.resources.camera_picker_open_settings_button
import reskiume.composeapp.generated.resources.camera_picker_permission_do_not_grant_permission_button
import reskiume.composeapp.generated.resources.camera_picker_permission_grant_permission_button
import reskiume.composeapp.generated.resources.camera_picker_permission_message
import reskiume.composeapp.generated.resources.camera_picker_permission_title
import reskiume.composeapp.generated.resources.create_account_screen_add_photo_message
import reskiume.composeapp.generated.resources.create_account_screen_delete_message
import reskiume.composeapp.generated.resources.create_account_screen_from_camera_message
import reskiume.composeapp.generated.resources.create_account_screen_from_gallery_message
import reskiume.composeapp.generated.resources.ic_add_photo
import reskiume.composeapp.generated.resources.ic_close

@Composable
fun RmAddPhoto(
    pickMultiplePhotosFromGallery: Boolean = false,
    onUriRetrieved: (String) -> Unit
) {

    var showAddPhoto by remember { mutableStateOf(true) }
    var showGallery by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var uri by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (showAddPhoto) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
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
            showAddPhoto = false
            InvokeCameraPicker { shouldShowCamera, uriProvided ->
                showCamera = shouldShowCamera
                if (uriProvided.isNotBlank()) {
                    showAddPhoto = false
                    uri = uriProvided
                    onUriRetrieved(uriProvided)
                } else {
                    showAddPhoto = true
                }
            }
        }

        if (uri.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier.wrapContentSize()
                            .padding(16.dp)
                            .background(color = secondaryRed, shape = RoundedCornerShape(15.dp))
                            .padding(8.dp)
                            .clickable {
                                showAddPhoto = true
                                uri = ""
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

@Composable
fun InvokeGalleryPicker(
    pickMultiplePhotosFromGallery: Boolean,
    onGalleryPickerResult: (Boolean, String) -> Unit
) {
    GalleryPickerLauncher(
        onPhotosSelected = { photos: List<GalleryPhotoResult> ->
            onGalleryPickerResult(false, photos.first().uri)
        },
        onError = { onGalleryPickerResult(false, "") },
        onDismiss = { onGalleryPickerResult(false, "") },
        allowMultiple = pickMultiplePhotosFromGallery,
        mimeTypes = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
        cameraCaptureConfig = CameraCaptureConfig(
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult: PhotoResult, onConfirm: (PhotoResult) -> Unit, onRetry: () -> Unit ->
                    onConfirm(photoResult)
                }
            ),
            compressionLevel = CompressionLevel.HIGH
        )
    )
}

@Composable
fun InvokeCameraPicker(onCameraPickerResult: (Boolean, String) -> Unit) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = { result: PhotoResult ->
                onCameraPickerResult(false, result.uri)
            },
            onError = {
                onCameraPickerResult(false, "")
            },
            onDismiss = {
                onCameraPickerResult(false, "")
            },
            cameraCaptureConfig = CameraCaptureConfig(
                compressionLevel = CompressionLevel.HIGH,
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm: (PhotoResult) -> Unit, onRetry: () -> Unit ->
                        onConfirm(photoResult)
                    },
                    customDeniedDialog = { onRetry: () -> Unit ->
                        RmDialog(
                            emoji = "📸",
                            title = stringResource(Res.string.camera_picker_permission_title),
                            message = stringResource(Res.string.camera_picker_permission_message),
                            allowMessage = stringResource(Res.string.camera_picker_permission_grant_permission_button),
                            denyMessage = stringResource(Res.string.camera_picker_permission_do_not_grant_permission_button),
                            onClickAllow = onRetry,
                            onClickDeny = {
                                onCameraPickerResult(false, "")
                            }
                        )
                    },
                    customSettingsDialog = { onOpenSettings: () -> Unit ->
                        RmDialog(
                            emoji = "⚙️",
                            title = stringResource(Res.string.camera_picker_go_to_settings_title),
                            message = stringResource(Res.string.camera_picker_grant_in_settings_message),
                            allowMessage = stringResource(Res.string.camera_picker_open_settings_button),
                            denyMessage = stringResource(Res.string.camera_picker_permission_do_not_grant_permission_button),
                            onClickAllow = onOpenSettings,
                            onClickDeny = {
                                onCameraPickerResult(false, "")
                            }
                        )
                    }
                )
            )
        )
    )
    Spacer(modifier = Modifier.height(80.dp))
}
