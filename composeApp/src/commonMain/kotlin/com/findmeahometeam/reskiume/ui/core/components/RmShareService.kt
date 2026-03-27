package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.findmeahometeam.reskiume.ui.fosterHomes.shareService.ShareService
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.share_service_install_message
import reskiume.composeapp.generated.resources.share_service_ok_button
import reskiume.composeapp.generated.resources.share_service_install_title

@Composable
fun RmShareService(
    shareServiceTitle: StringResource,
    vararg argumentsForSharingText: String
) {
    val shareService = koinInject<ShareService>()
    val shareServiceText = stringResource(
        shareServiceTitle,
        *argumentsForSharingText
    )
    var displayNoSharingAppError: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(shareServiceTitle) {

        shareService.shareContent(
            text = shareServiceText,
            onError = {
                displayNoSharingAppError = true
            }
        )
    }
    if (displayNoSharingAppError) {

        RmDialog(
            emoji = "📲",
            title = stringResource(Res.string.share_service_install_title),
            message = stringResource(Res.string.share_service_install_message),
            allowMessage = stringResource(Res.string.share_service_ok_button),
            onClickAllow = { displayNoSharingAppError = false },
            onClickDeny = { displayNoSharingAppError = false }
        )
    }
}
