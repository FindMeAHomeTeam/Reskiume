package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.profile.giveFeedback.GiveFeedback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_message
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_ok_button
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_title
import reskiume.composeapp.generated.resources.report_body
import reskiume.composeapp.generated.resources.report_element_link
import reskiume.composeapp.generated.resources.report_subject

@Composable
fun RmReport(
    reportedElementType: String,
    reportedElementTitle: String,
    reportedElementId: String
) {
    val giveFeedback: GiveFeedback = koinInject<GiveFeedback>()
    var displayNoEmailAppError: Boolean by rememberSaveable { mutableStateOf(false) }

    val sendReportSubject =
        stringResource(
            Res.string.report_subject,
            reportedElementType,
            reportedElementTitle,
            reportedElementId
        )
    val sendReportBody =
        stringResource(
            Res.string.report_body,
            reportedElementType,
            reportedElementTitle,
            reportedElementId
        )
    RmTextLink(
        modifier = Modifier.padding(10.dp),
        text = stringResource(Res.string.report_element_link, reportedElementType),
        textToLink = stringResource(Res.string.report_element_link, reportedElementType),
        linkColor = primaryRed,
        onClick = {
            giveFeedback.sendEmail(
                subject = sendReportSubject,
                body = sendReportBody,
                onError = {
                    displayNoEmailAppError = true
                }
            )
        }
    )
    if (displayNoEmailAppError) {
        RmDialog(
            emoji = "✉️",
            title = stringResource(Res.string.dialog_no_email_app_dialog_title),
            message = stringResource(Res.string.dialog_no_email_app_dialog_message),
            allowMessage = stringResource(Res.string.dialog_no_email_app_dialog_ok_button),
            onClickAllow = { displayNoEmailAppError = false },
            onClickDeny = { displayNoEmailAppError = false }
        )
    }
}
